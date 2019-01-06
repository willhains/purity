package com.willhains.purity;

import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static com.willhains.purity.Pair.toIndex;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class PairTest
{
	static final @Value class Height extends SingleDouble<Height> { Height(double raw) { super(raw, Height::new); } }
	static final @Value class Weight extends SingleDouble<Weight> { Weight(double raw) { super(raw, Weight::new); } }
	
	private final Height _height = new Height(173.5);
	private final Weight _weight = new Weight(79.4);
	
	@Test(expected = NullPointerException.class)
	public void shouldRejectNullLeftValue()
	{
		Pair.of(null, _weight);
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldRejectNullRightValue()
	{
		Pair.of(_height, null);
	}
	
	@Test
	public void shouldHoldRawValuesAfterConstruction()
	{
		final Pair<Height, Weight> bmi = Pair.of(_height, _weight);
		assertThat(bmi.left, is(_height));
		assertThat(bmi.right, is(_weight));
	}
	
	@Test
	public void shouldBeReflexive()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		assertTrue(x.equals(x));
	}
	
	@Test
	public void shouldBeSymmetric()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Pair<Height, Weight> y = Pair.of(_height, _weight);
		assertTrue(x.equals(y));
		assertTrue(y.equals(x));
		final Pair<Height, Weight> z = Pair.of(_height, _weight.plus(1));
		assertFalse(x.equals(z));
		assertFalse(z.equals(x));
	}
	
	@Test
	public void shouldBeTransitive()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Pair<Height, Weight> y = Pair.of(_height, _weight);
		final Pair<Height, Weight> z = Pair.of(_height, _weight);
		assertTrue(x.equals(y));
		assertTrue(y.equals(z));
		assertTrue(x.equals(z));
		final Pair<Height, Weight> w = Pair.of(_height, _weight.plus(1.0));
		assertFalse(w.equals(y));
		assertFalse(w.equals(z));
	}
	
	@Test
	public void shouldBeConsistent()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Pair<Height, Weight> y = Pair.of(_height, _weight);
		final Pair<Height, Weight> z = Pair.of(_height, _weight.plus(1.0));
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
		final int xHash1 = x.hashCode();
		final int xHash2 = x.hashCode();
		assertThat(xHash1, equalTo(xHash2));
	}
	
	@Test
	public void shouldHaveSameHashCode()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Pair<Height, Weight> y = Pair.of(_height, _weight);
		assertTrue(x.equals(y));
		final int xHash = x.hashCode();
		final int yHash = y.hashCode();
		assertThat(xHash, equalTo(yHash));
	}
	
	@Test
	public void shouldGenerateSameStringAsUnderlying()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		assertThat(x.toString(), containsString(x.left.toString()));
		assertThat(x.toString(), containsString(x.right.toString()));
	}
	
	@Test
	public void shouldSwitchLeftAndRight()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Pair<Weight, Height> y = x.flip();
		assertThat(y.left, is(x.right));
		assertThat(y.right, is(x.left));
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldTrapNullPredicateLeft()
	{
		Pair.of(_height, _weight).filterLeft(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldTrapNullPredicateRight()
	{
		Pair.of(_height, _weight).filterRight(null);
	}
	
	@Test
	public void shouldPassFilterLeftOnMatchingCondition()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Optional<Pair<Height, Weight>> opX = x.filterLeft($ -> $.isGreaterThan(50f));
		assertThat(opX.get(), is(x));
	}
	
	@Test
	public void shouldPassFilterRightOnMatchingCondition()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Optional<Pair<Height, Weight>> opX = x.filterRight($ -> $.isGreaterThan(50f));
		assertThat(opX.get(), is(x));
	}

	@Test
	public void shouldFailFilterLeftOnNonMatchingCondition()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Optional<Pair<Height, Weight>> opX = x.filterLeft($ -> $.isGreaterThan(1000f));
		assertFalse(opX.isPresent());
	}
	
	@Test
	public void shouldFailFilterRightOnNonMatchingCondition()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Optional<Pair<Height, Weight>> opX = x.filterRight($ -> $.isGreaterThan(1000f));
		assertFalse(opX.isPresent());
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldTrapNullMapperLeft()
	{
		Pair.of(_height, _weight).mapLeft(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldTrapNullMapperRight()
	{
		Pair.of(_height, _weight).mapRight(null);
	}

	@Test
	public void shouldMapLeftToNewValue()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Pair<Height, Weight> y = x.mapLeft(h -> h.plus(1));
		assertThat(y.left.raw, is(174.5));
		assertThat(y.right.raw, is(79.4));
	}
	
	@Test
	public void shouldMapRightToNewValue()
	{
		final Pair<Height, Weight> x = Pair.of(_height, _weight);
		final Pair<Height, Weight> y = x.mapRight(w -> w.plus(1));
		assertThat(y.left.raw, is(173.5));
		assertThat(y.right.raw, is(80.4));
	}
	
	@Test
	public void shouldUseLeftAsKeyAndRightAsValue()
	{
		final Stream<Pair<String, Integer>> pairStream = Stream.of(
			Pair.of("a", 1),
			Pair.of("b", 2));
		final Index<String, Integer> index = pairStream.collect(toIndex());
		assertThat(index.asMap().size(), is(2));
		assertThat(index.asMap().get("a"), is(1));
		assertThat(index.asMap().get("b"), is(2));
	}
}
