package com.willhains.purity;

import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class IndexTest
{
	@Test
	public void shouldCreateEmpty()
	{
		final Index<Object, Object> x = Index.empty();
		final Index<Object, Object> y = Index.copy(new HashMap<>());
		assertTrue(x.asMap().isEmpty());
		assertTrue(y.asMap().isEmpty());
		assertEquals(x, y);
		assertEquals(x.hashCode(), y.hashCode());
	}
	
	@Test
	public void shouldCreateWithOneElement()
	{
		final Index<String, String> x = Index.of("a", "x");
		final Index<String, String> y = Index.of("b", "y");
		assertThat(x.asMap().size(), is(1));
		assertNotEquals(x, y);
		assertThat(x.toString(), containsString("a"));
	}
	
	@Test
	public void shouldCopyMap()
	{
		final Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		final Index<String, Integer> x = Index.copy(map);
		assertThat(x.asMap(), is(equalTo(map)));
	}

	@Test
	public void shouldCopyIterableAsCollection()
	{
		final Iterable<Pair<String, Integer>> iterable = Arrays.asList(
			Pair.of("a", 1),
			Pair.of("b", 2));
		final Index<String, Integer> x = Index.copy(iterable);
		assertThat(x.asMap().get("a"), is(1));
		assertThat(x.asMap().get("b"), is(2));
	}
	
	@Test
	public void shouldCopyEmptyIterable()
	{
		final Iterable<Pair<String, Integer>> iterable = Arrays.asList();
		final Index<String, Integer> x = Index.copy(iterable);
		assertTrue(x.isEmpty());
	}
	
	@Test
	public void shouldNeverEqualNull()
	{
		final Index<String, Integer> x = Index.of("a", 1);
		assertFalse(x.equals(null));
	}
	
	@Test
	public void shouldNeverEqualAnotherClass()
	{
		final Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		final Index<String, Integer> x = Index.of("a", 1);
		assertFalse(x.equals(map));
	}

	@Test
	public void shouldBeImmutableAfterCopyConstructor()
	{
		final Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		final Index<String, Integer> x = Index.copy(map);
		map.put("a", 3);
		assertThat(x.asMap().get("a"), is(1));
	}
	
	@Test
	public void shouldIterateEntriesAsPairs()
	{
		final Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		final Index<String, Integer> x = Index.copy(map);
		final StringBuilder string = new StringBuilder();
		for(final Pair pair: x)
		{
			string.append(pair.left).append('=').append(pair.right).append('|');
		}
		assertThat(string.toString(), is("a=1|b=2|"));
	}
	
	@Test
	public void shouldCallbackOnEachKeyElement()
	{
		final Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		final Index<String, Integer> x = Index.copy(map);
		final StringBuilder string = new StringBuilder();
		x.forEach((key, element) -> string.append(key).append('=').append(element).append('|'));
		assertThat(string.toString(), is("a=1|b=2|"));
	}
	
	@Test
	public void shouldCallbackOnEachPair()
	{
		final Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		final Index<String, Integer> x = Index.copy(map);
		final StringBuilder string = new StringBuilder();
		x.forEach(pair -> string.append(pair.left).append('=').append(pair.right).append('|'));
		assertThat(string.toString(), is("a=1|b=2|"));
	}
	
	@Test
	public void shouldStreamKeyElementPairs()
	{
		final Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		final Index<String, Integer> x = Index.copy(map);
		final StringBuilder string = new StringBuilder();
		x.stream().forEach(pair -> string.append(pair.left).append('=').append(pair.right).append('|'));
		assertThat(string.toString(), is("a=1|b=2|"));
	}
	
	@Test
	public void shouldBeEmpty()
	{
		final Index<String, Integer> x = Index.empty();
		assertTrue(x.isEmpty());
		assertThat(x.size(), is(0));
	}
	
	@Test
	public void shouldBeNonEmpty()
	{
		final Index<String, Integer> x = Index.of("a", 1);
		assertFalse(x.isEmpty());
		assertThat(x.size(), is(1));
	}
	
	@Test
	public void shouldFindKey()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2);
		assertTrue(x.containsKey("a"));
		assertTrue(x.containsKey("b"));
		assertTrue(x.containsElement(1));
		assertTrue(x.containsElement(2));
		assertThat(x.get("a").get(), is(1));
		assertThat(x.get("b").get(), is(2));
	}
	
	@Test
	public void shouldNotFindKey()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2);
		assertFalse(x.containsKey("c"));
		assertFalse(x.containsElement(3));
		assertFalse(x.get("c").isPresent());
	}
	
	@Test
	public void shouldInvokeConsumerOnlyIfKeyExists()
	{
		final StringBuilder string = new StringBuilder();
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 3);
		x.ifPresent("a", string::append);
		x.ifPresent("b", string::append);
		x.ifPresent("c", string::append);
		x.ifPresent("d", string::append);
		assertThat(string.toString(), is("123"));
	}
	
	@Test
	public void shouldMutateMultipleIndexesIndependently()
	{
		final Index<String, Integer> x = Index.of("a", 1);
		final Index<String, Integer> y = x.append("b", 2);
		final Index<String, Integer> z = x.append("c", 3);
		assertThat(y, is(Index.of("a", 1).append("b", 2)));
		assertThat(z, is(Index.of("a", 1).append("c", 3)));
	}
	
	@Test
	public void shouldNotStackOverflowWhenAppendingManyElements()
	{
		final int stackSize = 100_000;
		Index<String, Integer> x = Index.empty();
		for(int i = 0; i < stackSize; i++) x = x.append(Integer.toString(i), i);
		assertThat(x.size(), is(stackSize));
	}
	
	@Test
	public void shouldAppendSingleElement()
	{
		final Index<String, Integer> x = Index.of("a", 1);
		final Index<String, Integer> y = x.append("b", 2);
		assertThat(y.get("a").get(), is(1));
		assertThat(y.get("b").get(), is(2));
		assertFalse(x.containsKey("b"));
	}
	
	@Test
	public void shouldOverwriteExistingKey()
	{
		final Index<String, Integer> x = Index.of("a", 1);
		final Index<String, Integer> y = x.append("a", 2);
		assertThat(x.get("a").get(), is(1));
		assertThat(y.get("a").get(), is(2));
	}
	
	@Test
	public void shouldAppendSinglePair()
	{
		final Index<String, Integer> x = Index.of("a", 1);
		final Index<String, Integer> y = x.append(Pair.of("b", 2));
		assertThat(y.get("a").get(), is(1));
		assertThat(y.get("b").get(), is(2));
		assertFalse(x.containsKey("b"));
	}
	
	@Test
	public void shouldAppendMultipleElementsFromIndex()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2);
		final Index<String, Integer> y = Index.of("b", 7).append("c", 8).append("d", 9);
		final Index<String, Integer> z = x.append(y);
		assertThat(z.get("a").get(), is(1));
		assertThat(z.get("b").get(), is(7));
		assertThat(z.get("c").get(), is(8));
		assertThat(z.get("d").get(), is(9));
	}
	
	@Test
	public void shouldAppendMultipleElementsFromMap()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2);
		final Map<String, Integer> y = new HashMap<>();
		y.put("b", 7);
		y.put("c", 8);
		y.put("d", 9);
		final Index<String, Integer> z = x.append(y);
		assertThat(z.get("a").get(), is(1));
		assertThat(z.get("b").get(), is(7));
		assertThat(z.get("c").get(), is(8));
		assertThat(z.get("d").get(), is(9));
	}
	
	@Test
	public void shouldDeleteSingleKey()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2);
		final Index<String, Integer> y = x.delete("a");
		assertThat(y, is(Index.of("b",2)));
	}
	
	@Test
	public void shouldDeleteMultipleKeys()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 3);
		final Index<String, Integer> y = x.delete(Plural.of("b", "c", "d"));
		assertThat(y.get("a").get(), is(1));
		assertFalse(y.get("b").isPresent());
		assertFalse(y.get("c").isPresent());
	}
	
	@Test
	public void shouldDeleteByPredicate()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 3);
		final Index<String, Integer> y = x.deleteIf((key, element) -> element > 1);
		assertThat(y, is(Index.of("a", 1)));
	}
	
	@Test
	public void shouldFilterByPredicate()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 3);
		final Index<String, Integer> y = x.filter((key, element) -> element > 2);
		assertThat(y, is(Index.of("c", 3)));
	}
	
	@Test
	public void shouldConvertEntries()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 3);
		final Index<String, Integer> y = x.map((key, element) -> Pair.of(key.toUpperCase(), -element));
		assertThat(y, is(Index.of("A", -1).append("B", -2).append("C", -3)));
	}
	
	@Test
	public void shouldConvertKeys()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 3);
		final Index<String, Integer> y = x.mapKeys(String::toUpperCase);
		assertThat(y, is(Index.of("A", 1).append("B", 2).append("C", 3)));
	}
	
	@Test
	public void shouldConvertElements()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 3);
		final Index<String, Integer> y = x.mapElements(element -> -element);
		assertThat(y, is(Index. of("a", -1).append("b", -2).append("c", -3)));
	}
	
	@Test
	public void shouldFlipWithOriginalEntriesSurviving()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 2);
		final Index<Integer, String> y = x.flip((first, second) -> first);
		assertThat(y, is(Index.of(1, "a").append(2, "b")));
	}
	
	@Test
	public void shouldFlipWithLatterEntriesSurviving()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 2);
		final Index<Integer, String> y = x.flip();
		assertThat(y, is(Index.of(1, "a").append(2, "c")));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotBeMutableViaKeysView()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 3);
		final Set<String> keys = x.keys();
		keys.remove("a");
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotBeMutableViaElementsView()
	{
		final Index<String, Integer> x = Index.of("a", 1).append("b", 2).append("c", 3);
		final Collection<Integer> elements = x.elements();
		elements.remove(1);
	}
}
