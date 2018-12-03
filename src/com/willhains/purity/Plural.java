package com.willhains.purity;

import java.util.*;
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
		Reading<Element> prepareForRead();
	}
	
	// The state where all mutations have been applied to the underlying collection, and it can now be read
	private static final @Value class Reading<@Value Element> implements MutationState<Element>
	{
		private final List<Element> _elements;
		Reading(final List<Element> elements) { _elements = elements; }
		@Override public Reading<Element> prepareForRead() { return this; }
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
	
	public int size() { return _prepareForRead().size(); }
	public boolean isEmpty() { return _prepareForRead().isEmpty(); }
	
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
}
