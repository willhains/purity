package com.willhains.purity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
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
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1)
	{
		final Map<Key, Element> map = singletonMap(key1, element1);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2)
	{
		final Map<Key, Element> map = new HashMap<>(2);
		map.put(key1, element1);
		map.put(key2, element2);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3)
	{
		final Map<Key, Element> map = new HashMap<>(3);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4)
	{
		final Map<Key, Element> map = new HashMap<>(4);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5)
	{
		final Map<Key, Element> map = new HashMap<>(5);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6)
	{
		final Map<Key, Element> map = new HashMap<>(6);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7)
	{
		final Map<Key, Element> map = new HashMap<>(7);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8)
	{
		final Map<Key, Element> map = new HashMap<>(8);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9)
	{
		final Map<Key, Element> map = new HashMap<>(9);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10)
	{
		final Map<Key, Element> map = new HashMap<>(10);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10,
		final Key key11, final Element element11)
	{
		final Map<Key, Element> map = new HashMap<>(11);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		map.put(key11, element11);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10,
		final Key key11, final Element element11,
		final Key key12, final Element element12)
	{
		final Map<Key, Element> map = new HashMap<>(12);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		map.put(key11, element11);
		map.put(key12, element12);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10,
		final Key key11, final Element element11,
		final Key key12, final Element element12,
		final Key key13, final Element element13)
	{
		final Map<Key, Element> map = new HashMap<>(13);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		map.put(key11, element11);
		map.put(key12, element12);
		map.put(key13, element13);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10,
		final Key key11, final Element element11,
		final Key key12, final Element element12,
		final Key key13, final Element element13,
		final Key key14, final Element element14)
	{
		final Map<Key, Element> map = new HashMap<>(14);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		map.put(key11, element11);
		map.put(key12, element12);
		map.put(key13, element13);
		map.put(key14, element14);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10,
		final Key key11, final Element element11,
		final Key key12, final Element element12,
		final Key key13, final Element element13,
		final Key key14, final Element element14,
		final Key key15, final Element element15)
	{
		final Map<Key, Element> map = new HashMap<>(15);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		map.put(key11, element11);
		map.put(key12, element12);
		map.put(key13, element13);
		map.put(key14, element14);
		map.put(key15, element15);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10,
		final Key key11, final Element element11,
		final Key key12, final Element element12,
		final Key key13, final Element element13,
		final Key key14, final Element element14,
		final Key key15, final Element element15,
		final Key key16, final Element element16)
	{
		final Map<Key, Element> map = new HashMap<>(16);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		map.put(key11, element11);
		map.put(key12, element12);
		map.put(key13, element13);
		map.put(key14, element14);
		map.put(key15, element15);
		map.put(key16, element16);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10,
		final Key key11, final Element element11,
		final Key key12, final Element element12,
		final Key key13, final Element element13,
		final Key key14, final Element element14,
		final Key key15, final Element element15,
		final Key key16, final Element element16,
		final Key key17, final Element element17)
	{
		final Map<Key, Element> map = new HashMap<>(17);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		map.put(key11, element11);
		map.put(key12, element12);
		map.put(key13, element13);
		map.put(key14, element14);
		map.put(key15, element15);
		map.put(key16, element16);
		map.put(key17, element17);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10,
		final Key key11, final Element element11,
		final Key key12, final Element element12,
		final Key key13, final Element element13,
		final Key key14, final Element element14,
		final Key key15, final Element element15,
		final Key key16, final Element element16,
		final Key key17, final Element element17,
		final Key key18, final Element element18)
	{
		final Map<Key, Element> map = new HashMap<>(18);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		map.put(key11, element11);
		map.put(key12, element12);
		map.put(key13, element13);
		map.put(key14, element14);
		map.put(key15, element15);
		map.put(key16, element16);
		map.put(key17, element17);
		map.put(key18, element18);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10,
		final Key key11, final Element element11,
		final Key key12, final Element element12,
		final Key key13, final Element element13,
		final Key key14, final Element element14,
		final Key key15, final Element element15,
		final Key key16, final Element element16,
		final Key key17, final Element element17,
		final Key key18, final Element element18,
		final Key key19, final Element element19)
	{
		final Map<Key, Element> map = new HashMap<>(19);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		map.put(key11, element11);
		map.put(key12, element12);
		map.put(key13, element13);
		map.put(key14, element14);
		map.put(key15, element15);
		map.put(key16, element16);
		map.put(key17, element17);
		map.put(key18, element18);
		map.put(key19, element19);
		return new Index<>(new Reading<>(map));
	}
	
	public static <@Value Key, @Value Element> Index<Key, Element> of(
		final Key key1, final Element element1,
		final Key key2, final Element element2,
		final Key key3, final Element element3,
		final Key key4, final Element element4,
		final Key key5, final Element element5,
		final Key key6, final Element element6,
		final Key key7, final Element element7,
		final Key key8, final Element element8,
		final Key key9, final Element element9,
		final Key key10, final Element element10,
		final Key key11, final Element element11,
		final Key key12, final Element element12,
		final Key key13, final Element element13,
		final Key key14, final Element element14,
		final Key key15, final Element element15,
		final Key key16, final Element element16,
		final Key key17, final Element element17,
		final Key key18, final Element element18,
		final Key key19, final Element element19,
		final Key key20, final Element element20)
	{
		final Map<Key, Element> map = new HashMap<>(20);
		map.put(key1, element1);
		map.put(key2, element2);
		map.put(key3, element3);
		map.put(key4, element4);
		map.put(key5, element5);
		map.put(key6, element6);
		map.put(key7, element7);
		map.put(key8, element8);
		map.put(key9, element9);
		map.put(key10, element10);
		map.put(key11, element11);
		map.put(key12, element12);
		map.put(key13, element13);
		map.put(key14, element14);
		map.put(key15, element15);
		map.put(key16, element16);
		map.put(key17, element17);
		map.put(key18, element18);
		map.put(key19, element19);
		map.put(key20, element20);
		return new Index<>(new Reading<>(map));
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
	public void forEach(final BiConsumer<Key, Element> action) { _prepareForRead().forEach(action); }
	public Iterator<Pair<Key, Element>> iterator() { return stream().iterator(); }
	
	public Stream<Pair<Key, Element>> stream()
	{
		return asMap().entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue()));
	}
	
	public int size() { return _prepareForRead().size(); }
	public boolean isEmpty() { return _prepareForRead().isEmpty(); }
	public boolean containsKey(final Key key) { return _prepareForRead().containsKey(key); }
	public boolean containsElement(final Element element) { return _prepareForRead().containsValue(element); }
	public Element get(final Key elementForKey) { return _prepareForRead().get(elementForKey); }
}
