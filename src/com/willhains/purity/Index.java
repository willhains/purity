package com.willhains.purity;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableMap;

/**
 * An immutable indexed collection of elements, that can be treated as a {@link Value}, so long as the {@link Key}s and
 * {@link Element}s are {@link Value}s.
 *
 * @author willhains
 * @param <Key> the type of key used to index the elements.
 * @param <Element> the type of each element contained within.
 */
public final @Value class Index<@Value Key, @Value Element> implements Iterable<Pair<Key, Element>>
{
	private static final Index<?, ?> _EMPTY = new Index<>(new Reading<>(Collections.emptyMap()));
	
	/** @return an empty {@link Index}. */
	public static <@Value Key, @Value Element> Index<Key, Element> empty()
	{
		@SuppressWarnings("unchecked") final Index<Key, Element> empty = (Index<Key, Element>)_EMPTY;
		return empty;
	}
	
	/** @return an {@link Index} with a single element. */
	public static <@Value Key, @Value Element> Index<Key, Element> of(final Key key, final Element element)
	{
		final Map<Key, Element> map = singletonMap(key, element);
		return new Index<>(new Reading<>(map));
	}
	
	/**
	 * Index a series of {@link Pair}s by their left side.
	 *
	 * @see Pair#toIndex()
	 */
	public static <@Value Key, @Value Element> Index<Key, Element> copy(final Iterable<Pair<Key, Element>> pairs)
	{
		final Map<Key, Element> map = new HashMap<>();
		pairs.forEach(pair -> map.put(pair.left, pair.right));
		if(map.isEmpty()) return empty();
		return new Index<>(new Reading<>(map));
	}
	
	/** Copy a {@link Map} as am {@link Index}. */
	public static <@Value Key, @Value Element> Index<Key, Element> copy(final Map<Key, Element> elements)
	{
		if(elements.isEmpty()) return empty();
		return new Index<>(new Reading<>(new HashMap<>(elements)));
	}
	
	// An Index may be in one of two states: Reading, or Mutating. An Index in Mutating state may change to the
	// equivalent Reading state, but not the reverse. The Reading state is always the final state of an Index.
	// A Mutating state has exactly one possible equivalent Reading state. The Mutating state itself cannot change.
	// This property is mutable and non-volatile, because even if multiple threads observe it in a different state,
	// each can only mutate it to the same eventual Reading state, so all threads always observe the same result.
	private MutationState<Key, Element> _state;
	private Index(final MutationState<Key, Element> state) { _state = state; }
	
	@Override
	public boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final Index<?,?> that = (Index<?,?>)other;
		return Single.equals(this._prepareForRead(), that._prepareForRead());
	}
	
	@Override public int hashCode() { return Single.hashCode(_prepareForRead()); }
	@Override public String toString() { return Single.toString(_prepareForRead()); }
	
	private @Value interface MutationState<@Value Key, @Value Element>
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
		default Reading<Key, Element> prepareForRead() { return new Reading<>(prepareForWrite()); }
		
		/**
		 * Create a mutable {@link Map} copy of the data, and apply the mutations to it.
		 *
		 * @return the mutated data as a {@link Map}.
		 */
		Map<Key, Element> prepareForWrite();
	}
	
	// The state where all mutations have been applied to the underlying collection, and it can now be read
	private static final @Value class Reading<@Value Key, @Value Element> implements MutationState<Key, Element>
	{
		private final Map<Key, Element> _elements;
		Reading(final Map<Key, Element> elements) { _elements = elements; }
		@Override public int generation() { return 0; }
		@Override public Reading<Key, Element> prepareForRead() { return this; }
		@Override public Map<Key, Element> prepareForWrite() { return new HashMap<>(_elements); }
	}
	
	// Apply all mutations, collapsing them to the resulting collection, then return that collection
	private Map<Key, Element> _prepareForRead()
	{
		final Reading<Key, Element> state = _state.prepareForRead();
		if(state != _state) _state = state;
		return state._elements;
	}
	
	/** @return an immutable {@link List} containing the elements of this {@link Plural}. */
	public Map<Key, Element> asMap() { return unmodifiableMap(_prepareForRead()); }
	
	/** @see Map#forEach */
	public void forEach(final BiConsumer<Key, Element> action) { _prepareForRead().forEach(action); }
	
	/** @return an {@link Iterator} over key-element {@link Pair}s. */
	public Iterator<Pair<Key, Element>> iterator() { return stream().iterator(); }
	
	/** @return a {@link Stream} of key-element {@link Pair}s. */
	public Stream<Pair<Key, Element>> stream()
	{
		return asMap().entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue()));
	}
	
	/** @return the element indexed by {@code elementForKey}, or empty if there is none. */
	public Optional<Element> get(final Key elementForKey)
	{
		return Optional.ofNullable(_prepareForRead().get(elementForKey));
	}
	
	public int size() { return _prepareForRead().size(); }
	public boolean isEmpty() { return _prepareForRead().isEmpty(); }
	public boolean containsKey(final Key key) { return _prepareForRead().containsKey(key); }
	public boolean containsElement(final Element element) { return _prepareForRead().containsValue(element); }
	public void ifPresent(final Key key, final Consumer<Element> then) { get(key).ifPresent(then); }
	
	/// Mutations ///
	
	private static final class Mutating<@Value Key, @Value Element, @Value ConvertedKey, @Value ConvertedElement>
		implements MutationState<ConvertedKey, ConvertedElement>
	{
		private static final int _MAX_GENERATION = 4096;
		private final MutationState<Key, Element> _inner;
		private final Function<Map<Key, Element>, Map<ConvertedKey, ConvertedElement>> _mutator;
		private final int _generation;
		
		Mutating(
			final MutationState<Key, Element> inner,
			final Function<Map<Key, Element>, Map<ConvertedKey, ConvertedElement>> mutator)
		{
			_inner = inner.generation() > _MAX_GENERATION ? inner.prepareForRead() : inner;
			_mutator = mutator;
			_generation = _inner.generation() + 1;
		}
		
		@Override public int generation() { return _generation; }
		@Override public Map<ConvertedKey, ConvertedElement> prepareForWrite()
		{
			return _mutator.apply(_inner.prepareForWrite());
		}
	}
	
	private Index<Key, Element> _mutate(final Consumer<Map<Key, Element>> mutator)
	{
		return new Index<>(new Mutating<>(_state, map ->
		{
			mutator.accept(map);
			return map;
		}));
	}
	
	private <@Value ConvertedKey, @Value ConvertedElement> Index<ConvertedKey, ConvertedElement> _transform(
		final Function<Map<Key, Element>, Map<ConvertedKey, ConvertedElement>> transformer)
	{
		return new Index<>(new Mutating<>(_state, transformer));
	}
	
	public Index<Key, Element> append(final Key key, final Element element)
	{
		return _mutate(map -> map.put(key, element));
	}
	
	public Index<Key, Element> append(final Pair<Key, Element> pair)
	{
		return _mutate(map -> map.put(pair.left, pair.right));
	}
	
	public Index<Key, Element> append(final Index<Key, Element> elements)
	{
		return append(elements._prepareForRead());
	}
	
	public Index<Key, Element> append(final Map<Key, Element> elements)
	{
		return _mutate(map -> map.putAll(elements));
	}
	
	public Index<Key, Element> delete(final Key key) { return _mutate(map -> map.remove(key)); }
	public Index<Key, Element> delete(final Plural<Key> keys) { return deleteIf((key, $) -> keys.contains(key)); }
	
	/** Delete elements where the key and element satisfy the {@code where} condition. */
	public Index<Key, Element> deleteIf(final BiPredicate<Key, Element> where)
	{
		return _mutate(map -> map.entrySet().removeIf(entry -> where.test(entry.getKey(), entry.getValue())));
	}
	
	/** Delete elements where the key and element do not satisfy the {@code where} condition. */
	public Index<Key, Element> filter(final BiPredicate<Key, Element> where) { return deleteIf(where.negate()); }
	
	/** Convert the keys to new values using the mapper function. When two are the same, the latter will survive. */
	public <@Value Converted> Index<Converted, Element> mapKeys(final BiFunction<Key, Element, Converted> mapper)
	{
		return _transform(before ->
		{
			final Map<Converted, Element> after = new HashMap<>(before.size());
			before.forEach((key, element) -> after.put(mapper.apply(key, element), element));
			return after;
		});
	}
	
	/** Convert the elements to new values using the mapper function. */
	public <@Value Converted> Index<Key, Converted> mapElements(final BiFunction<Key, Element, Converted> mapper)
	{
		return _transform(before ->
		{
			final Map<Key, Converted> after = new HashMap<>(before.size());
			before.forEach((key, element) -> after.put(key, mapper.apply(key, element)));
			return after;
		});
	}
	
	/** Same as {@link #flip(BinaryOperator)}, where the last mapping of hte same value survives. */
	public Index<Element, Key> flip()
	{
		return flip((first, second) -> second);
	}
	
	/** Reverse the index so that elements are keys, and vice-versa, with {@code combiner} to handle duplicates. */
	public Index<Element, Key> flip(final BinaryOperator<Key> combiner)
	{
		return _transform(before ->
		{
			final Map<Element, Key> after = new HashMap<>(before.size());
			before.forEach((key2, element) -> after.compute(element, ($, key1) ->
				key1 == null ? key2 : combiner.apply(key1, key2)));
			return after;
		});
	}
}
