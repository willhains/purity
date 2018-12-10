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
		final Index<String, String> x = Index.of(Pair.of("a", "x"));
		final Index<String, String> y = Index.of(Pair.of("b", "y"));
		assertThat(x.asMap().size(), is(1));
		assertNotEquals(x, y);
		assertThat(x.toString(), containsString("a"));
	}
	
	@Test
	public void shouldCreateWithMoreElements()
	{
		final Index<String, Integer> x0 = Index.of();
		final Index<String, Integer> x1 = Index.of(Pair.of("a", 1));
		final Index<String, Integer> x2 = Index.of(Pair.of("a", 1), Pair.of("b", 2));
		assertTrue(x0.asMap().isEmpty());
		assertThat(x1.asMap().toString(), containsString("a"));
		assertThat(x1.asMap().toString(), containsString("1"));
		assertThat(x2.asMap().toString(), containsString("b"));
		assertThat(x2.asMap().toString(), containsString("2"));
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
}
