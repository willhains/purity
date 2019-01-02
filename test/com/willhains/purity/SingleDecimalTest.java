package com.willhains.purity;

import org.junit.Test;

import java.math.BigDecimal;

import static com.willhains.purity.Rule.rules;
import static com.willhains.purity.SingleNumber.$;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SingleDecimalTest
{
	static final @Value class Price extends SingleDecimal<Price>
	{
		Price(final BigDecimal p) { super(p, Price::new); }
		Price(final double p) { super(p, Price::new); }
		Price(final String p) { super(p, Price::new); }
	}
	
	@Test
	public void shouldConstructFromDouble()
	{
		final Price x = new Price(new BigDecimal(12.3));
		final Price y = new Price(12.3);
		assertEquals(x, y);
	}
	
	@Test
	public void shouldConstructFromString()
	{
		final Price x = new Price(new BigDecimal("12.3"));
		final Price y = new Price("12.3");
		assertEquals(x, y);
	}
	
	@Test
	public void shouldRepresentRawAsNumber()
	{
		final Price x = new Price("12.3");
		assertThat(x.asNumber(), is(x.raw));
	}
	
	@Test
	public void shouldNotUseScientificNotationByDefault()
	{
		final Price x = new Price("123456000000000000");
		assertThat(x.toString(), is("123456000000000000"));
		final Price y = new Price("0.000000000000000123");
		assertThat(y.toString(), is("0.000000000000000123"));
	}
	
	@Test
	public void shouldCompareLarger()
	{
		final Price x = new Price("10.0");
		final Price y = new Price("5.0");
		assertTrue(x.compareTo(y) > 0);
		assertTrue(x.compareToNumber(y.raw) > 0);
	}
	
	@Test
	public void shouldCompareSmaller()
	{
		final Price x = new Price("5.0");
		final Price y = new Price("10.0");
		assertTrue(x.compareTo(y) < 0);
		assertTrue(x.compareToNumber(y.raw) < 0);
	}
	
	@Test
	public void shouldCompareEqual()
	{
		final Price x = new Price("10.0");
		final Price y = new Price("10.0");
		assertEquals(x, y);
		assertTrue(x.compareTo(y) == 0);
		assertTrue(x.compareToNumber(y.raw) == 0);
	}
	
	@Test
	public void shouldIdentifyZero()
	{
		final Price x = new Price("0.0");
		assertTrue(x.isZero());
		assertFalse(x.isNonZero());
		assertFalse(x.isPositive());
		assertFalse(x.isNegative());
	}
	
	@Test
	public void shouldIdentifyPositive()
	{
		final Price x = new Price("0.1");
		assertFalse(x.isZero());
		assertTrue(x.isNonZero());
		assertTrue(x.isPositive());
		assertFalse(x.isNegative());
	}
	
	@Test
	public void shouldIdentifyNegative()
	{
		@Value class Factor extends SingleDecimal<Factor> { Factor(BigDecimal factor) { super(factor, Factor::new); } }
		final Factor x = new Factor($(-0.1));
		assertFalse(x.isZero());
		assertTrue(x.isNonZero());
		assertFalse(x.isPositive());
		assertTrue(x.isNegative());
	}
	
	@Test public void shouldAdd()
	{
		assertThat(new Price("12.3").plus("0.7").raw, is($("13.0")));
	}
	
	@Test public void shouldSubtract()
	{
		assertThat(new Price("12.3").minus("0.3").raw, is($("12.0")));
	}
	
	@Test public void shouldMultiply()
	{
		assertThat(new Price("12.3").multiplyBy("10").raw, is($("123.0")));
	}
	
	@Test public void shouldDivide()
	{
		assertThat(new Price("12.8").divideBy(2).raw, is($("6.4")));
	}
	
	@Test
	public void shouldRoundHalfUp()
	{
		assertThat(new Price("14.5").round().raw, is($("15")));
		assertThat(new Price("14.4").round().raw, is($("14")));
	}
	
	@Test
	public void shouldRoundDown()
	{
		assertThat(new Price("14.9").roundDown().raw, is($("14")));
	}
	
	@Test
	public void shouldRoundUp()
	{
		assertThat(new Price("14.1").roundUp().raw, is($("15")));
	}
	
	@Test
	public void shouldAcceptBetweenInclusive()
	{
		class A extends SingleDecimal<A>
		{
			A(BigDecimal a) { super(a, A::new, Rule.rules(min($(2)), max($(5)))); }
		}
		new A($(2));
		new A($(5));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanExclusive()
	{
		class A extends SingleDecimal<A> { A(BigDecimal a) { super(a, A::new, min($(2))); } }
		new A($(1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanExclusive()
	{
		class A extends SingleDecimal<A> { A(BigDecimal a) { super(a, A::new, max($(5))); } }
		new A($(6));
	}
	
	@Test
	public void shouldAcceptBetweenExclusive()
	{
		class A extends SingleDecimal<A>
		{
			private A(BigDecimal a) { super(a, A::new); }
			A(String a) { super(a, A::new, rules(greaterThan($(2)), lessThan($(5)))); }
		}
		new A("3");
		new A("4");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanInclusive()
	{
		class A extends SingleDecimal<A> { A(BigDecimal a) { super(a, A::new, greaterThan($(2))); } }
		new A($(2));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanInclusive()
	{
		class A extends SingleDecimal<A> { A(BigDecimal a) { super(a, A::new, lessThan($(5))); } }
		new A($(5));
	}
	
	@Test
	public void shouldPassThroughValueWithinRange()
	{
		class A extends SingleDecimal<A>
		{
			private A(BigDecimal a) { super(a, A::new); }
			A(double a) { super(a, A::new, rules(floor($(2)), ceiling($(5)))); }
		}
		assertThat(new A(3.0).raw, is($(3)));
	}
	
	@Test
	public void shouldAdjustValueBelowFloor()
	{
		class A extends SingleDecimal<A> { A(BigDecimal a) { super(a, A::new, floor($(2))); } }
		assertThat(new A($(1)).raw, is($(2)));
	}
	
	@Test
	public void shouldAdjustValueAboveCeiling()
	{
		class A extends SingleDecimal<A> { A(BigDecimal a) { super(a, A::new, ceiling($(5))); } }
		assertThat(new A($(6)).raw, is($(5)));
	}
	
	@Test
	public void shouldRoundToSpecifiedNumberOfPlaces()
	{
		assertThat(new Price("14").roundToPrecision(4).raw, is($("14.0000")));
		assertThat(new Price("12.345").roundToPrecision(2).raw, is($("12.35")));
		assertThat(new Price("12.345").roundToPrecision(3).raw, is($("12.345")));
		assertThat(new Price("12345.6").roundToPrecision(0).raw, is($("12346")));
	}
}
