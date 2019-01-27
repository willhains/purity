package com.willhains.purity;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

/**
 * An immutable ordered collection of elements, that can be treated as a {@link Pure}, so long as the {@link Element}s
 * are {@link Pure}s.
 *
 * @author willhains
 * @param <Element> the type of each element contained within.
 */
public final @Pure class Plural<@Pure Element> implements Iterable<Element>
{
	private static final Plural<?> _EMPTY = new Plural<>(new Reading<>(Collections.emptyList()));
	
	// Core List factories
	private static <E> List<E> newList() { return new ArrayList<>(); }
	private static <E> List<E> newList(final Collection<E> withElements) { return new ArrayList<>(withElements); }
	private static <E> List<E> newList(final int withCapacity) { return new ArrayList<>(withCapacity); }
	
	/** @return an empty {@link Plural}. */
	public static <@Pure Element> Plural<Element> empty()
	{
		@SuppressWarnings("unchecked") final Plural<Element> empty = (Plural<Element>)_EMPTY;
		return empty;
	}
	
	/** @return {@link #empty()} if {@code possiblyNullElement} is {@code null}. */
	public static <@Pure Element> Plural<Element> ofNullable(final Element possiblyNullElement)
	{
		return possiblyNullElement == null ? empty() : Plural.of(possiblyNullElement);
	}
	
	/**
	 * Wrap a single element in a {@link Plural}.
	 *
	 * @param element the single element to populate the plural.
	 * @param <E> the type of the element.
	 * @return a {@link Plural} containing the specified single element.
	 */
	public static <@Pure E> Plural<E> of(E element) { return new Plural<>(new Reading<>(singletonList(element))); }
	
	/** Copy {@code elements} into a new {@link Plural} value. */
	@SafeVarargs
	public static <@Pure Element> Plural<Element> of(Element... elements)
	{
		final Element[] array = elements.clone();
		switch(array.length)
		{
			case 0: return empty();
			case 1: return of(array[0]);
			default: return new Plural<>(new Reading<>(Arrays.asList(array)));
		}
	}
	
	/** Copy {@code elements} into a new {@link Plural} value. */
	public static <@Pure Element> Plural<Element> copy(final Iterable<Element> elements)
	{
		if(elements instanceof Plural) return (Plural<Element>)elements;
		if(elements instanceof Set) return copy((Set)elements);
		if(elements instanceof Collection) return copy((Collection<Element>)elements);
		
		final List<Element> list = newList();
		elements.forEach(list::add);
		if(list.isEmpty()) return empty();
		return new Plural<>(new Reading<>(list));
	}
	
	/** Copy {@code elements} into a new {@link Plural} value. */
	public static <@Pure Element> Plural<Element> copy(final Set<Element> elements)
	{
		if(elements.isEmpty()) return empty();
		return new Plural<>(new Reading<>(newList(elements)), true);
	}
	
	/** Copy {@code elements} into a new {@link Plural} value. */
	public static <@Pure Element> Plural<Element> copy(final Collection<Element> elements)
	{
		if(elements.isEmpty()) return empty();
		return new Plural<>(new Reading<>(newList(elements)));
	}
	
	/** @return a {@link Collector} that wraps the contents in a {@link Plural}. */
	public static <@Pure Element> Collector<Element, ?, Plural<Element>> toPlural()
	{
		return collectingAndThen(toList(), list -> new Plural<>(new Reading<>(list)));
	}
	
	// A Plural may be in one of two states: Reading, or Mutating. A Plural in Mutating state may change to the
	// equivalent Reading state, but not the reverse. The Reading state is always the final state of a Plural.
	// A Mutating state has exactly one possible equivalent Reading state. The Mutating state itself cannot change.
	// This property is mutable and non-volatile, because even if multiple threads observe it in a different state,
	// each can only mutate it to the same eventual Reading state, so all threads always observe the same result.
	private final boolean _distinct;
	private MutationState<Element> _state;
	private Plural(final MutationState<Element> state) { this(state, false); }
	private Plural(final MutationState<Element> state, final boolean distinct) { _state = state; _distinct = distinct; }
	
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
	
	private @Pure interface MutationState<@Pure Element>
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
		default Reading<Element> prepareForRead() { return new Reading<>(prepareForWrite()); }
		
		/**
		 * Create a mutable {@link Map} copy of the data, and apply the mutations to it.
		 *
		 * @return the mutated data as a {@link Map}.
		 */
		List<Element> prepareForWrite();
	}
	
	// The state where all mutations have been applied to the underlying collection, and it can now be read
	private static final @Pure class Reading<@Pure Element> implements MutationState<Element>
	{
		private final List<Element> _elements;
		Reading(final List<Element> elements) { _elements = elements; }
		@Override public int generation() { return 0; }
		@Override public Reading<Element> prepareForRead() { return this; }
		@Override public List<Element> prepareForWrite() { return newList(_elements); }
	}
	
	// Apply all mutations, collapsing them to the resulting collection, then return that collection
	private List<Element> _prepareForRead()
	{
		final Reading<Element> state = _state.prepareForRead();
		if(state != _state) _state = state;
		return state._elements;
	}
	
	/** @return an immutable {@link List} containing the elements of this {@link Plural}. */
	public List<Element> asList() { return unmodifiableList(_prepareForRead()); }
	
	/** @return an immutable {@link Set} containing the distinct elements of this {@link Plural}. */
	public Set<Element> asSet() { return unmodifiableSet(_index()); }
	
	@Override public Iterator<Element> iterator() { return asList().iterator(); }
	public ListIterator<Element> listIterator() { return asList().listIterator(); }
	public Stream<Element> stream() { return _prepareForRead().stream(); }
	
	public Element get(int elementAtIndex) { return _prepareForRead().get(elementAtIndex); }
	public int size() { return _prepareForRead().size(); }
	public boolean isEmpty() { return _prepareForRead().isEmpty(); }
	public boolean allMatch(final Predicate<Element> condition) { return stream().allMatch(condition); }
	public boolean anyMatch(final Predicate<Element> condition) { return stream().anyMatch(condition); }
	public boolean noneMatch(final Predicate<Element> condition) { return stream().noneMatch(condition); }
	public Optional<Element> maxBy(final Comparator<Element> comparator) { return stream().max(comparator); }
	public Optional<Element> minBy(final Comparator<Element> comparator) { return stream().min(comparator); }
	
	/** @return the index of the first element that satisfies the predicate; empty if none do. */
	public OptionalInt indexOf(final Predicate<Element> where)
	{
		final List<Element> list = _prepareForRead();
		for(int i = 0; i < list.size(); i++)
		{
			final Element element = list.get(i);
			if(where.test(element)) return OptionalInt.of(i);
		}
		return OptionalInt.empty();
	}
	
	/** @return the index of the last element that satisfies the predicate; empty if none do. */
	public OptionalInt lastIndexOf(final Predicate<Element> where)
	{
		final List<Element> list = _prepareForRead();
		for(int i = list.size() - 1; i >= 0; i--)
		{
			final Element element = list.get(i);
			if(where.test(element)) return OptionalInt.of(i);
		}
		return OptionalInt.empty();
	}
	
	/** @return the first element of this collection; empty if the collection is empty. */
	public Optional<Element> getFirst()
	{
		final List<Element> list = _prepareForRead();
		return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
	}
	
	/** @return the last element of this collection; empty if the collection is empty. */
	public Optional<Element> getLast()
	{
		final List<Element> list = _prepareForRead();
		final int size = list.size();
		return size == 0 ? Optional.empty() : Optional.of(list.get(size - 1));
	}
	
	/** @return a string containing all the elements, separated by a delimiter. */
	public String join(final CharSequence delim) { return stream().map(Object::toString).collect(joining(delim)); }
	
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
	
	private static final class Mutating<@Pure Element, @Pure Converted> implements MutationState<Converted>
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
	public Plural<Element> append(final Plural<Element> p) { return _mutate(list -> list.addAll(p._prepareForRead())); }
	public Plural<Element> append(final Collection<Element> c) { return _mutate(list -> list.addAll(c)); }
	public Plural<Element> delete(final Element element) { return _mutate(list -> list.remove(element)); }
	public Plural<Element> deleteAll(final Collection<Element> c) { return _mutate(list -> list.removeAll(c)); }
	public Plural<Element> deleteIf(final Predicate<Element> where) { return _mutate(list -> list.removeIf(where)); }
	public Plural<Element> filter(final Predicate<Element> where) { return deleteIf(where.negate()); }
	public Plural<Element> reverse() { return _mutate(Collections::reverse); }
	
	public Plural<Element> deleteAll(final Plural<Element> p)
	{
		return _mutate(list -> list.removeAll(p._index == null ? p._prepareForRead() : p._index()));
	}
	
	/** @return a {@link Plural} containing only the set of unique elements (by {@link Object#equals}). */
	public Plural<Element> distinct()
	{
		if(_distinct) return this;
		return new Plural<>(new Mutating<>(_state, list -> list.stream().distinct().collect(toList())), true);
	}
	
	/** @return a new {@link Plural} with the first element removed. */
	public Plural<Element> deleteFirst()
	{
		return _mutate(list -> { if(!list.isEmpty()) list.remove(0); });
	}
	
	/** @return a new {@link Plural} with the last element removed. */
	public Plural<Element> deleteLast()
	{
		return _mutate(list -> { if(!list.isEmpty()) list.remove(list.size() - 1); });
	}
	
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
	
	/** @return a new {@link Plural}, with the specified element inserted at the specified index. */
	public Plural<Element> insert(final Element element, int atIndex)
	{
		if(atIndex < 0) throw new IndexOutOfBoundsException("atIndex(" + atIndex + ") < 0");
		return _mutate(list ->
		{
			final int index = Math.min(atIndex, list.size());
			list.add(index, element);
		});
	}
	
	/**
	 * @param elementAtIndex the index to replace. When greater than the max index, this method does nothing.
	 * @return a new {@link Plural}, with the element at the specified index replaced with the specified new value.
	 */
	public Plural<Element> replace(final int elementAtIndex, final Element withNewValue)
	{
		if(elementAtIndex < 0) throw new IndexOutOfBoundsException("elementAtIndex(" + elementAtIndex + ") < 0");
		return _mutate(list ->
		{
			if(elementAtIndex >= list.size()) return;
			list.set(elementAtIndex, withNewValue);
		});
	}
	
	/** @return a new {@link Plural}, with the element at the specified index removed. */
	public Plural<Element> delete(final int atIndex)
	{
		if(atIndex < 0) throw new IndexOutOfBoundsException("atIndex(" + atIndex + ") < 0");
		return _mutate(list -> { if(atIndex < list.size()) list.remove(atIndex); });
	}
	
	/** @return a new {@link Plural}, with the elements transformed by a mapper function. */
	public <@Pure Converted> Plural<Converted> map(final Function<Element, Converted> mapper)
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
	public <@Pure Converted> Plural<Converted> flatMap(final Function<Element, Plural<Converted>> mapper)
	{
		return _transform(list ->
		{
			final List<Converted> converted = newList();
			list.forEach(element -> converted.addAll(mapper.apply(element)._prepareForRead()));
			return converted;
		});
	}
	
	/**
	 * Zip together two {@link Plural}s into one, where the elements are {@link Pair}s of corresponding index positions.
	 */
	public <@Pure Right> Plural<Pair<Element, Right>> zip(final Plural<Right> rightElements)
	{
		return _transform(left ->
		{
			final List<Right> right = rightElements._prepareForRead();
			final int zipSize = Math.min(left.size(), right.size());
			final List<Pair<Element, Right>> zipped = newList(zipSize);
			for(int i = 0; i < zipSize; i++) zipped.add(Pair.of(left.get(i), right.get(i)));
			return zipped;
		});
	}
	
	/** @return a new {@link Plural}, sorted by the specified comparator order. */
	public Plural<Element> sortedBy(final Comparator<Element> order) { return _mutate(list -> sort(list, order)); }
	
	/** @return a new {@link Plural}, sorted by the {@link Comparable} element's order. */
	public <Property extends Comparable<Property>> Plural<Element> sortedBy(final Function<Element, Property> property)
	{
		return sortedBy(comparing(property));
	}
	
	/** @return a new {@link Plural}, sorted by the elements' natural order. */
	public Plural<Element> sorted()
	{
		return sortedBy((a, b) ->
		{
			if(a instanceof Comparable)
			{
				@SuppressWarnings("unchecked") final Comparable<Element> comparableA = (Comparable<Element>)a;
				return comparableA.compareTo(b);
			}
			return a.toString().compareTo(b.toString());
		});
	}
}
