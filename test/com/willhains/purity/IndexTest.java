package com.willhains.purity;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
}
