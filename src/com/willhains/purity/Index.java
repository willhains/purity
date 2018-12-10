package com.willhains.purity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableMap;

/**
 * An immutable indexed collection of elements, that can be treated as a {@link Value}, so long as the {@link Key}s and
 * {@link Element}s are {@link Value}s.
 *
 * @author willhains
 * @param <Key> the type of key used to index the elements.
 * @param <Element> the type of each element contained within.
 */
public final @Value class Index<@Value Key, @Value Element>
{
	private static final Index<?, ?> _EMPTY = new Index<>(new Reading<>(Collections.emptyMap()));
	
	/** @return an empty {@link Index}. */
	public static <@Value Key, @Value Element> Index<Key, Element> empty()
	{
		@SuppressWarnings("unchecked") final Index<Key, Element> empty = (Index<Key, Element>)_EMPTY;
		return empty;
	}
	
	/**
	 * Index a series of {@link Pair}s by their left side.
	 *
	 * @see Pair#toIndex()
	 */
	public static <@Value Key, @Value Element> Index<Key, Element> of(final Pair<Key, Element>... pairs)
	{
		return Stream.of(pairs).collect(Pair.toIndex());
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> copy(final Iterable<Pair<Key, Element>> pairs)
	{
		final Map<Key, Element> map = new HashMap<>();
		pairs.forEach(pair -> map.put(pair.left, pair.right));
		if(map.isEmpty()) return empty();
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> copy(final Map<Key, Element> elements)
	{
		if(elements.isEmpty()) return empty();
		return new Index<>(new Reading<>(new HashMap<>(elements)));
	}
	
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
		Reading<Key, Element> prepareForRead();
	}
	
	private static final @Value class Reading<@Value Key, @Value Element> implements MutationState<Key, Element>
	{
		private final Map<Key, Element> _elements;
		Reading(final Map<Key, Element> elements) { _elements = elements; }
		@Override public Reading<Key, Element> prepareForRead() { return this; }
	}
	
	private Map<Key, Element> _prepareForRead()
	{
		final Reading<Key, Element> state = _state.prepareForRead();
		_state = state;
		return state._elements;
	}
	
	public Map<Key, Element> asMap() { return unmodifiableMap(_prepareForRead()); }
}
