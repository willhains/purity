package com.willhains.purity;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

/**
 * An immutable indexed collection of values, that can be treated as a {@link Pure} value, so long as the {@link Key}s
 * and {@link Value}s are {@link Pure} values.
 *
 * @author willhains
 * @param <Key> the type of key used to index the values.
 * @param <Value> the type of each value contained within.
 */
public final @Pure class Index<@Pure Key, @Pure Value> implements Iterable<Pair<Key, Value>>
{
	private static final Index<?, ?> _EMPTY = new Index<>(new Reading<>(Collections.emptyMap()));
	
	// Core Map factories
	private static <K, V> Map<K, V> newMap() { return new LinkedHashMap<>(); }
	private static <K, V> Map<K, V> newMap(final Map<K, V> withEntries) { return new LinkedHashMap<>(withEntries); }
	private static <K, V> Map<K, V> newMap(final int withCapacity) { return new LinkedHashMap<>(withCapacity); }
	
	/** @return an empty {@link Index}. */
	public static <@Pure Key, @Pure Value> Index<Key, Value> empty()
	{
		@SuppressWarnings("unchecked") final Index<Key, Value> empty = (Index<Key, Value>)_EMPTY;
		return empty;
	}
	
	/** @return an {@link Index} with a single value. */
	public static <@Pure Key, @Pure Value> Index<Key, Value> of(final Key key, final Value value)
	{
		final Map<Key, Value> map = singletonMap(key, value);
		return new Index<>(new Reading<>(map));
	}
	
	/**
	 * Index a series of {@link Pair}s by their left side.
	 *
	 * @see Pair#toIndex()
	 */
	public static <@Pure Key, @Pure Value> Index<Key, Value> copy(final Iterable<Pair<Key, Value>> pairs)
	{
		final Map<Key, Value> map = newMap();
		pairs.forEach(pair -> map.put(pair.left, pair.right));
		if(map.isEmpty()) return empty();
		return new Index<>(new Reading<>(map));
	}
	
	/** Copy a {@link Map} as am {@link Index}. */
	public static <@Pure Key, @Pure Value> Index<Key, Value> copy(final Map<Key, Value> entries)
	{
		if(entries.isEmpty()) return empty();
		return new Index<>(new Reading<>(newMap(entries)));
	}
	
	// An Index may be in one of two states: Reading, or Mutating. An Index in Mutating state may change to the
	// equivalent Reading state, but not the reverse. The Reading state is always the final state of an Index.
	// A Mutating state has exactly one possible equivalent Reading state. The Mutating state itself cannot change.
	// This property is mutable and non-volatile, because even if multiple threads observe it in a different state,
	// each can only mutate it to the same eventual Reading state, so all threads always observe the same result.
	private MutationState<Key, Value> _state;
	private Index(final MutationState<Key, Value> state) { _state = state; }
	
	@Override
	public boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		final Index<?,?> that = (Index<?,?>)other;
		return Single.equals(this._prepareForRead(), that._prepareForRead());
	}
	
	@Override public int hashCode() { return Single.hashCode(_prepareForRead()); }
	@Override public String toString() { return Single.toString(_prepareForRead()); }
	
	private @Pure interface MutationState<@Pure Key, @Pure Value>
	{
		/**
		 * The number of mutation wrappers applied. Automatically collapse when this gets up to a certain threshold, to
		 * avoid a stack overflow.
		 */
		int generation();
		
		/**
		 * Apply all pending mutations, collapsing to a single {@link Reading} state.
		 *
		 * @return the resulting {@link Reading} state.
		 */
		default Reading<Key, Value> prepareForRead() { return new Reading<>(prepareForWrite()); }
		
		/**
		 * Create a mutable {@link Map} copy of the data, and apply the mutations to it.
		 *
		 * @return the mutated data as a {@link Map}.
		 */
		Map<Key, Value> prepareForWrite();
	}
	
	// The state where all mutations have been applied to the underlying collection, and it can now be read
	private static final @Pure class Reading<@Pure Key, @Pure Value> implements MutationState<Key, Value>
	{
		private final Map<Key, Value> _entries;
		Reading(final Map<Key, Value> entries) { _entries = entries; }
		@Override public int generation() { return 0; }
		@Override public Reading<Key, Value> prepareForRead() { return this; }
		@Override public Map<Key, Value> prepareForWrite() { return newMap(_entries); }
	}
	
	// Apply all mutations, collapsing them to the resulting collection, then return that collection
	private Map<Key, Value> _prepareForRead()
	{
		final Reading<Key, Value> state = _state.prepareForRead();
		if(state != _state) _state = state;
		return state._entries;
	}
	
	/** @return an immutable {@link List} containing the entries of this {@link Plural}. */
	public Map<Key, Value> asMap() { return unmodifiableMap(_prepareForRead()); }
	
	/** @see Map#forEach */
	public void forEach(final BiConsumer<Key, Value> action) { _prepareForRead().forEach(action); }
	
	/** @return an {@link Iterator} over key-value {@link Pair}s. */
	public Iterator<Pair<Key, Value>> iterator() { return stream().iterator(); }
	
	/** @return a {@link Stream} of key-value {@link Pair}s. */
	public Stream<Pair<Key, Value>> stream()
	{
		return asMap().entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue()));
	}
	
	/** @return the value indexed by {@code valueForKey}, or empty if there is none. */
	public Optional<Value> get(final Key valueForKey)
	{
		return Optional.ofNullable(_prepareForRead().get(valueForKey));
	}
	
	public int size() { return _prepareForRead().size(); }
	public boolean isEmpty() { return _prepareForRead().isEmpty(); }
	public boolean containsKey(final Key key) { return _prepareForRead().containsKey(key); }
	public boolean containsValue(final Value value) { return _prepareForRead().containsValue(value); }
	public void ifPresent(final Key key, final Consumer<Value> then) { get(key).ifPresent(then); }
	
	public Set<Key> keys() { return Collections.unmodifiableSet(_prepareForRead().keySet()); }
	public Collection<Value> values() { return Collections.unmodifiableCollection(_prepareForRead().values()); }
	
	/// Mutations ///
	
	private static final class Mutating<@Pure Key, @Pure Value, @Pure ConvertedKey, @Pure ConvertedValue>
		implements MutationState<ConvertedKey, ConvertedValue>
	{
		private static final int _MAX_GENERATION = 4096;
		private final MutationState<Key, Value> _inner;
		private final Function<Map<Key, Value>, Map<ConvertedKey, ConvertedValue>> _mutator;
		private final int _generation;
		
		Mutating(
			final MutationState<Key, Value> inner,
			final Function<Map<Key, Value>, Map<ConvertedKey, ConvertedValue>> mutator)
		{
			_inner = inner.generation() > _MAX_GENERATION ? inner.prepareForRead() : inner;
			_mutator = mutator;
			_generation = _inner.generation() + 1;
		}
		
		@Override public int generation() { return _generation; }
		@Override public Map<ConvertedKey, ConvertedValue> prepareForWrite()
		{
			return _mutator.apply(_inner.prepareForWrite());
		}
	}
	
	private Index<Key, Value> _mutate(final Consumer<Map<Key, Value>> mutator)
	{
		return new Index<>(new Mutating<>(_state, map ->
		{
			mutator.accept(map);
			return map;
		}));
	}
	
	private <@Pure ConvertedKey, @Pure ConvertedValue> Index<ConvertedKey, ConvertedValue> _transform(
		final Function<Map<Key, Value>, Map<ConvertedKey, ConvertedValue>> transformer)
	{
		return new Index<>(new Mutating<>(_state, transformer));
	}
	
	public Index<Key, Value> set(final Key key, final Value value)
	{
		return _mutate(map -> map.put(key, value));
	}
	
	public Index<Key, Value> set(final Pair<Key, Value> pair)
	{
		return _mutate(map -> map.put(pair.left, pair.right));
	}
	
	public Index<Key, Value> setAll(final Index<? extends Key, ? extends Value> entries)
	{
		return setAll(entries._prepareForRead());
	}
	
	public Index<Key, Value> setAll(final Map<? extends Key, ? extends Value> entries)
	{
		return _mutate(map -> map.putAll(entries));
	}
	
	public Index<Key, Value> setIfAbsent(Key key, Supplier<? extends Value> valueSupplier)
	{
		requireNonNull(valueSupplier);
		return _mutate(map -> map.computeIfAbsent(key, $ -> valueSupplier.get()));
	}
	
	public Index<Key, Value> replaceIfPresent(Key key, Function<? super Value, ? extends Value> valueReplacer)
	{
		requireNonNull(valueReplacer);
		return _mutate(map -> map.computeIfPresent(key, ($, oldValue) -> valueReplacer.apply(oldValue)));
	}
	
	public Index<Key, Value> delete(final Key key) { return _mutate(map -> map.remove(key)); }
	public Index<Key, Value> deleteAll(final Plural<Key> keys) { return deleteIf((key, $) -> keys.contains(key)); }
	
	/** Delete entries where the key and value satisfy the {@code where} condition. */
	public Index<Key, Value> deleteIf(final BiPredicate<Key, Value> where)
	{
		return _mutate(map -> map.entrySet().removeIf(entry -> where.test(entry.getKey(), entry.getValue())));
	}
	
	/** Delete entries where the key and value do not satisfy the {@code where} condition. */
	public Index<Key, Value> filter(final BiPredicate<Key, Value> where) { return deleteIf(where.negate()); }
	
	/**
	 * Convert the keys and values to new values using the mapper functions.
	 * When two resulting keys are the same, the latter survives.
	 */
	public <@Pure ConvertedKey, @Pure ConvertedValue> Index<ConvertedKey, ConvertedValue> map(
		final BiFunction<Key, Value, Pair<ConvertedKey, ConvertedValue>> mapper)
	{
		return _transform(before ->
		{
			final Map<ConvertedKey, ConvertedValue> after = newMap(before.size());
			before.forEach((key, value) ->
			{
				final Pair<ConvertedKey, ConvertedValue> newPair = mapper.apply(key, value);
				after.put(newPair.left, newPair.right);
			});
			return after;
		});
	}
	
	/** Convert the keys to new values using the mapper function. When two are the same, the latter will survive. */
	public <@Pure Converted> Index<Converted, Value> mapKeys(final Function<Key, Converted> mapper)
	{
		return _transform(before ->
		{
			final Map<Converted, Value> after = newMap(before.size());
			before.forEach((key, value) -> after.put(mapper.apply(key), value));
			return after;
		});
	}
	
	/** Convert the values to new values using the mapper function. */
	public <@Pure Converted> Index<Key, Converted> mapValues(final Function<Value, Converted> mapper)
	{
		return _transform(before ->
		{
			final Map<Key, Converted> after = newMap(before.size());
			before.forEach((key, value) -> after.put(key, mapper.apply(value)));
			return after;
		});
	}
	
	/** Same as {@link #flip(BinaryOperator)}, where the last mapping of the same value survives. */
	public Index<Value, Key> flip()
	{
		return flip((first, second) -> second);
	}
	
	/** Reverse the index so that values are keys, and vice-versa, with {@code combiner} to handle duplicates. */
	public Index<Value, Key> flip(final BinaryOperator<Key> combiner)
	{
		return _transform(before ->
		{
			final Map<Value, Key> after = newMap(before.size());
			before.forEach((key2, value) -> after.compute(value, ($, key1) ->
				key1 == null ? key2 : combiner.apply(key1, key2)));
			return after;
		});
	}
}
