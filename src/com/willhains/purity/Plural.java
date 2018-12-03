package com.willhains.purity;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * An immutable ordered collection of elements, that can be treated as a {@link Value}, so long as the {@link Element}s
 * are {@link Value}s.
 *
 * @author willhains
 * @param <Element> the type of each element contained within.
 */
public final @Value class Plural<@Value Element> implements Iterable<Element>
{
	private static final Plural<?> _EMPTY = new Plural<>(new Reading<>(Collections.emptyList()));
	
	/** @return an empty {@link Plural}. */
	public static <@Value Element> Plural<Element> empty()
	{
		@SuppressWarnings("unchecked") final Plural<Element> empty = (Plural<Element>)_EMPTY;
		return empty;
	}
	
	/**
	 * @param e1 (e2, e3, ...) elements of the collection.
	 * @param <E> the type of elements.
	 * @return a {@link Plural} containing the specified elements.
	 */
	// @formatter:off
	public static <@Value E> Plural<E> of(E e1) { return new Plural<>(new Reading<>(singletonList(e1))); }
	public static <@Value E> Plural<E> of(E e1, E e2) { return _wrap(e1, e2); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3) { return _wrap(e1, e2, e3); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4) { return _wrap(e1, e2, e3, e4); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5) { return _wrap(e1, e2, e3, e4, e5); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6) { return _wrap(e1, e2, e3, e4, e5, e6); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) { return _wrap(e1, e2, e3, e4, e5, e6, e7); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12, E e13) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12, E e13, E e14) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12, E e13, E e14, E e15) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12, E e13, E e14, E e15, E e16) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12, E e13, E e14, E e15, E e16, E e17) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12, E e13, E e14, E e15, E e16, E e17, E e18) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17, e18); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12, E e13, E e14, E e15, E e16, E e17, E e18, E e19) { return _wrap(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17, e18, e19); }
	public static <@Value E> Plural<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12, E e13, E e14, E e15, E e16, E e17, E e18, E e19, E e20, E... more)
	// @formatter:on
	{
		final int argsLength = 20;
		if(more.length > Integer.MAX_VALUE - argsLength) throw new IllegalArgumentException("too many elements");
		final Object[] objectArray = new Object[argsLength + more.length];
		objectArray[0] = e1;
		objectArray[1] = e2;
		objectArray[2] = e3;
		objectArray[3] = e4;
		objectArray[4] = e5;
		objectArray[5] = e6;
		objectArray[6] = e7;
		objectArray[7] = e8;
		objectArray[8] = e9;
		objectArray[9] = e10;
		objectArray[10] = e11;
		objectArray[11] = e12;
		objectArray[12] = e13;
		objectArray[13] = e14;
		objectArray[14] = e15;
		objectArray[15] = e16;
		objectArray[16] = e17;
		objectArray[17] = e18;
		objectArray[18] = e19;
		objectArray[19] = e20;
		System.arraycopy(more, 0, objectArray, argsLength, more.length);
		@SuppressWarnings("unchecked") final E[] elementArray = (E[])objectArray;
		return _wrap(elementArray);
	}
	
	// Wrap the elements array in a Plural without defensive copy
	@SafeVarargs
	private static <@Value Element> Plural<Element> _wrap(final Element... elements)
	{
		switch(elements.length)
		{
			case 0: return empty();
			case 1: return of(elements[0]);
			default: return new Plural<>(new Reading<>(Arrays.asList(elements)));
		}
	}
	
	/** Copy {@code elements} into a new {@link Plural} value. */
	@SafeVarargs
	public static <@Value Element> Plural<Element> copy(final Element... elements)
	{
		return _wrap(elements.clone());
	}
	
	/** Copy {@code elements} into a new {@link Plural} value. */
	public static <@Value Element> Plural<Element> copy(final Iterable<Element> elements)
	{
		if(elements instanceof Plural) return (Plural<Element>)elements;
		if(elements instanceof Collection) return copy((Collection<Element>)elements);
		final ArrayList<Element> list = new ArrayList<>();
		elements.forEach(list::add);
		if(list.isEmpty()) return empty();
		return new Plural<>(new Reading<>(list));
	}
	
	/** Copy {@code elements} into a new {@link Plural} value. */
	public static <@Value Element> Plural<Element> copy(final Collection<Element> elements)
	{
		if(elements.isEmpty()) return empty();
		return new Plural<>(new Reading<>(new ArrayList<>(elements)));
	}
	
	/** @return a {@link Collector} that wraps the contents in a {@link Plural}. */
	public static <@Value Element> Collector<Element, ?, Plural<Element>> toPlural()
	{
		return collectingAndThen(toList(), list -> new Plural<>(new Reading<>(list)));
	}
	
	// A Plural may be in one of two states: Reading, or Mutating. A Plural in Mutating state may change to the
	// equivalent Reading state, but not the reverse. The Reading state is always the final state of a Plural.
	// A Mutating state has exactly one possible equivalent Reading state. The Mutating state itself cannot change.
	// This property is mutable and non-volatile, because even if multiple threads observe it in a different state,
	// each can only mutate it to the same eventual Reading state, so all threads always observe the same result.
	private MutationState<Element> _state;
	private Plural(final MutationState<Element> state) { _state = state; }
	
	@Override
	public boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final Plural<?> that = (Plural<?>)other;
		return Single.equals(this._prepareForRead(), that._prepareForRead());
	}
	
	@Override public int hashCode() { return Single.hashCode(_prepareForRead()); }
	@Override public String toString() { return Single.toString(_prepareForRead()); }
	
	private @Value interface MutationState<@Value Element>
	{
		int generation();
		default Reading<Element> prepareForRead() { return new Reading<>(prepareForWrite()); }
		List<Element> prepareForWrite();
	}
	
	// The state where all mutations have been applied to the underlying collection, and it can now be read
	private static final @Value class Reading<@Value Element> implements MutationState<Element>
	{
		private final List<Element> _elements;
		Reading(final List<Element> elements) { _elements = elements; }
		@Override public int generation() { return 0; }
		@Override public Reading<Element> prepareForRead() { return this; }
		@Override public List<Element> prepareForWrite() { return new ArrayList<>(_elements); }
	}
	
	// Apply all mutations, collapsing them to the resulting collection, then return that collection
	private List<Element> _prepareForRead()
	{
		final Reading<Element> state = _state.prepareForRead();
		_state = state;
		return state._elements;
	}
	
	/** @return an immutable {@link List} containing the elements of this {@link Plural}. */
	public List<Element> asList() { return unmodifiableList(_prepareForRead()); }
	
	/** @return an immutable {@link Set} containing the distinct elements of this {@link Plural}. */
	public Set<Element> asSet() { return unmodifiableSet(_index()); }
	
	@Override public Iterator<Element> iterator() { return asList().iterator(); }
	public Stream<Element> stream() { return _prepareForRead().stream(); }
	
	public Element get(int elementAtIndex) { return _prepareForRead().get(elementAtIndex); }
	public int size() { return _prepareForRead().size(); }
	public boolean isEmpty() { return _prepareForRead().isEmpty(); }
	
	/** Apply {@code reducer} repeatedly to summarise all the elements as a single value. */
	public <Result> Result reduce(final Result initialValue, final BiFunction<Result, Element, Result> reducer)
	{
		Result result = initialValue;
		for(final Element element: this) { result = reducer.apply(result, element); }
		return result;
	}
	
	/// Lazy index of entries for fast contains() operation ///
	
	private Set<Element> _index;
	private Set<Element> _index() { return _index == null ? _index = new HashSet<>(_prepareForRead()) : _index; }
	
	/**
	 * Search the elements for one that equals the specified element.
	 * Performance for repeated searches of the same {@link Plural} should be significantly faster than a {@link List}.
	 *
	 * @param element the element for which to search. Matched by {@link Object#equals}.
	 * @return {@code true} if the element was present in the elements; {@code false} if not.
	 */
	public boolean contains(final Element element) { return _index().contains(element); }
	public boolean containsAll(final Element... elements) { return _index().containsAll(Arrays.asList(elements)); }
	public boolean containsAll(final Collection<Element> elements) { return _index().containsAll(elements); }
	
	/// Mutations ///
	
	private static final class Mutating<@Value Element, @Value Converted> implements MutationState<Converted>
	{
		private static final int _MAX_GENERATION = 4096;
		private final MutationState<Element> _inner;
		private final Function<List<Element>, List<Converted>> _mutator;
		private final int _generation;
		
		Mutating(final MutationState<Element> inner, final Function<List<Element>, List<Converted>> mutator)
		{
			_inner = inner.generation() > _MAX_GENERATION ? inner.prepareForRead() : inner;
			_mutator = mutator;
			_generation = _inner.generation() + 1;
		}
		
		@Override public int generation() { return _generation; }
		@Override public List<Converted> prepareForWrite() { return _mutator.apply(_inner.prepareForWrite()); }
	}
	
	private Plural<Element> _mutate(final Consumer<List<Element>> mutator)
	{
		return new Plural<>(new Mutating<>(_state, list ->
		{
			mutator.accept(list);
			return list;
		}));
	}
	
	private <Converted> Plural<Converted> _transform(final Function<List<Element>, List<Converted>> transformer)
	{
		return new Plural<>(new Mutating<>(_state, transformer));
	}
	
	public Plural<Element> append(final Element element) { return _mutate(list -> list.add(element)); }
	public Plural<Element> delete(final Element element) { return _mutate(list -> list.remove(element)); }
	public Plural<Element> deleteIf(final Predicate<Element> where) { return _mutate(list -> list.removeIf(where)); }
	public Plural<Element> filter(final Predicate<Element> where) { return deleteIf(where.negate()); }
	
	/**
	 * @param start the index from which to start the new {@link Plural}.
	 * @return a new {@link Plural} subset of this, starting at the specified index.
	 */
	public Plural<Element> fromIndex(final int start)
	{
		if(start < 0) throw new IndexOutOfBoundsException("start(" + start + ") < 0");
		return _transform(list ->
		{
			final int end = list.size();
			return list.subList(Math.min(end, start), end);
		});
	}
	
	/**
	 * @param length the length to which to start the new {@link Plural}.
	 * @return a new {@link Plural} subset of this, from index zero up to the specified length.
	 */
	public Plural<Element> truncate(final int length)
	{
		if(length < 0) throw new IndexOutOfBoundsException("length(" + length + ") < 0");
		return _transform(list ->
		{
			final int end = Math.min(list.size(), length);
			return list.subList(0, end);
		});
	}
	
	/** @return a new {@link Plural}, with the elements transformed by a mapper function. */
	public <@Value Converted> Plural<Converted> map(final Function<Element, Converted> mapper)
	{
		return _transform(list ->
		{
			@SuppressWarnings("unchecked") final List<Object> before = (List<Object>)list;
			for(final ListIterator<Object> i = before.listIterator(); i.hasNext();)
			{
				@SuppressWarnings("unchecked") final Element element = (Element)i.next();
				i.set(mapper.apply(element));
			}
			@SuppressWarnings("unchecked") final List<Converted> after = (List<Converted>)before;
			return after;
		});
	}
	
	/** @return a new {@link Plural}, with the elements transformed by a mapper function. */
	public <@Value Converted> Plural<Converted> flatMap(final Function<Element, Plural<Converted>> mapper)
	{
		return _transform(list ->
		{
			final List<Converted> converted = new ArrayList<>();
			list.forEach(element -> converted.addAll(mapper.apply(element)._prepareForRead()));
			return converted;
		});
	}
}
