package com.willhains.purity;

import org.junit.Test;

import java.util.Optional;

import static com.willhains.purity.DoubleRule.rules;
import static com.willhains.purity.DoubleRule.validUnless;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SingleDoubleTest
{
	public static final @Value class Height extends SingleDouble<Height>
	{
		public Height(final double rawValue) { super(rawValue, Height::new); }
	}
	
	@Test
	public void shouldReturnRawValueAfterConstruction()
	{
		assertThat(new Height(17.3).raw, equalTo(17.3));
	}
	
	@Test
	public void shouldAlwaysBeUnequalToNull()
	{
		final Height x = new Height(12.3);
		assertFalse(x.equals(null));
	}
	
	@Test
	public void shouldAlwaysBeUnequalToDifferentClass()
	{
		class Height2 extends SingleDouble<Height2> { public Height2(double x) { super(x, Height2::new); } }
		final Height x = new Height(12.3);
		final Height2 y = new Height2(12.3);
		assertFalse(x.equals(y));
	}
	
	@Test
	public void shouldBeReflexive()
	{
		final Height x = new Height(12.3);
		assertTrue(x.equals(x));
	}
	
	@Test
	public void shouldBeSymmetric()
	{
		final Height x = new Height(10.0);
		final Height y = new Height(10.0);
		assertTrue(x.equals(y));
		assertTrue(y.equals(x));
		final Height z = new Height(10.1);
		assertFalse(x.equals(z));
		assertFalse(z.equals(x));
	}
	
	@Test
	public void shouldBeTransitive()
	{
		final Height x = new Height(10.0);
		final Height y = new Height(10.0);
		final Height z = new Height(10.0);
		assertTrue(x.equals(y));
		assertTrue(y.equals(z));
		assertTrue(x.equals(z));
		final Height w = new Height(10.1);
		assertFalse(w.equals(y));
		assertFalse(w.equals(z));
	}
	
	@Test
	public void shouldBeConsistent()
	{
		final Height x = new Height(10.0);
		final Height y = new Height(10.0);
		final Height z = new Height(10.1);
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
		final double xHash1 = x.hashCode();
		final double xHash2 = x.hashCode();
		assertThat(xHash1, equalTo(xHash2));
	}
	
	@Test
	public void shouldHaveSameHashCode()
	{
		final Height x = new Height(10.0);
		final Height y = new Height(10.0);
		assertTrue(x.equals(y));
		final double xHash = x.hashCode();
		final double yHash = y.hashCode();
		assertThat(xHash, equalTo(yHash));
	}
	
	@Test
	public void shouldGenerateSameStringAsUnderlying()
	{
		final Height x = new Height(10.0);
		assertThat(x.toString(), equalTo("10.0"));
	}
	
	@Test
	public void shouldAcceptRealNumber()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, realNumber); } }
		new A(1.0);
		new A(Double.MIN_VALUE);
		new A(Double.MAX_VALUE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapNaN()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, realNumber); } }
		new A(Double.NaN);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapNegativeInfinity()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, realNumber); } }
		new A(Double.NEGATIVE_INFINITY);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapPositiveInfinity()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, realNumber); } }
		new A(Double.POSITIVE_INFINITY);
	}
	
	@Test
	public void shouldAcceptBetweenInclusive()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, rules(min(2.0), max(5.0))); } }
		new A(2.0);
		new A(5.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanExclusive()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, min(2.0)); } }
		new A(1.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanExclusive()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, max(5.0)); } }
		new A(6.0);
	}
	
	@Test
	public void shouldAcceptBetweenExclusive()
	{
		class A extends SingleDouble<A> { A(double a)
		{
			super(a, A::new, rules(greaterThan(2.0), lessThan(5.0))); }
		}
		new A(3.0);
		new A(4.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanInclusive()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, greaterThan(2.0)); } }
		new A(2.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanInclusive()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, lessThan(5.0)); } }
		new A(5.0);
	}
	
	@Test
	public void shouldPassThroughValueWithinRange()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, rules(floor(2.0), ceiling(5.0))); } }
		assertThat(new A(3.0).raw, is(3.0));
	}
	
	@Test
	public void shouldAdjustValueBelowFloor()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, floor(2.0)); } }
		assertThat(new A(1.0).raw, is(2.0));
	}
	
	@Test
	public void shouldAdjustValueAboveCeiling()
	{
		class A extends SingleDouble<A> { A(double a) { super(a, A::new, ceiling(5.0)); } }
		assertThat(new A(6.0).raw, is(5.0));
	}
	
	@Test
	public void shouldTestRawValue()
	{
		final Height x = new Height(10.0);
		assertTrue(x.is(i -> i < 100.0));
		assertFalse(x.is(i -> i > 100.0));
	}
	
	@Test
	public void shouldTestRawValueAndNegate()
	{
		final Height x = new Height(10.0);
		assertFalse(x.isNot(i -> i < 100.0));
		assertTrue(x.isNot(i -> i > 100.0));
	}
	
	@Test
	public void shouldPassFilterOnMatchingCondition()
	{
		final Height x = new Height(10.0);
		final Optional<Height> opX = x.filter(Double::isFinite);
		assertThat(opX.get(), is(x));
	}
	
	@Test
	public void shouldFailFilterOnNonMatchingCondition()
	{
		final Height x = new Height(10.0);
		final Optional<Height> opX = x.filter(Double::isInfinite);
		assertFalse(opX.isPresent());
	}
	
	@Test
	public void shouldMapToNewValue()
	{
		final Height x = new Height(10.0);
		final Height y = x.map(f -> f + 0.1);
		assertThat(y.raw, equalTo(10.1));
	}
	
	@Test
	public void shouldMapToSameValue()
	{
		final Height x = new Height(10.0);
		final Height y = x.map(f -> f + 0d);
		assertEquals(x, y);
	}
	
	@Test
	public void shouldFlatMapToNewValue()
	{
		final Height x = new Height(10.0);
		final Height y = x.flatMap(f -> new Height(f + 0.1));
		assertThat(y.raw, equalTo(10.1));
	}
	
	@Test
	public void customRules()
	{
		class A extends SingleDouble<A>
		{
			A(double a) { super(a, A::new, validUnless(raw -> raw % 2 > 0, "Must be even")); }
		}
		new A(2d);
	}
	
	@Test
	public void shouldRoundHalfUp()
	{
		assertThat(new Height(14.5).round().raw, is(15.0));
		assertThat(new Height(14.4).round().raw, is(14.0));
	}
	
	@Test
	public void shouldRoundDown()
	{
		assertThat(new Height(14.9).roundDown().raw, is(14.0));
	}
	
	@Test
	public void shouldRoundUp()
	{
		assertThat(new Height(14.1).roundUp().raw, is(15.0));
	}
}
