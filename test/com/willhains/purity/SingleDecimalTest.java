package com.willhains.purity;

import org.junit.Test;

import java.math.BigDecimal;

import static com.willhains.purity.Rule.rules;
import static com.willhains.purity.SingleDecimal.$;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
	public void shouldNotUseScientificNotationByDefault()
	{
		final Price x = new Price("123456000000000000");
		assertThat(x.toString(), is("123456000000000000"));
		final Price y = new Price("0.000000000000000123");
		assertThat(y.toString(), is("0.000000000000000123"));
	}
	
	@Test
	public void shouldConvertAnythingToDecimal()
	{
		assertThat($("12345.67890"), is(new BigDecimal("12345.67890")));
		assertThat($(12.3450), is(new BigDecimal("12.345")));
		assertThat($(6), is(new BigDecimal("6")));
		class Name extends SingleString<Name> { Name(final String x) { super(x, Name::new); } }
		assertThat($(new Name("12.345")), is(new BigDecimal("12.345")));
	}
	
	@Test
	public void shouldKeepBigDecimal()
	{
		final BigDecimal x = new BigDecimal("123.4500");
		assertThat($(x), is(sameInstance(x)));
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
}
