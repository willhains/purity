package com.willhains.purity;

import com.willhains.purity.annotations.*;
import org.junit.Test;

import java.math.BigDecimal;

import static com.willhains.purity.SingleNumber.$;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SingleDecimalTest
{
	static final @Pure class Price extends SingleDecimal<Price>
	{
		Price(final BigDecimal p) { super(p, Price::new); }
		Price(final double p) { super(p, Price::new); }
		Price(final String p) { super(p, Price::new); }
	}
	
	@Test
	public void shouldConstructFromDouble()
	{
		final Price x = new Price(BigDecimal.valueOf(12.3));
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
	
	static final @Pure class Factor extends SingleDecimal<Factor>
	{
		Factor(BigDecimal factor) { super(factor, Factor::new); }
	}
	
	@Test
	public void shouldIdentifyNegative()
	{
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

	@Validate(min = 2, max = 5)
	static final class A extends SingleDecimal<A> { A(BigDecimal a) { super(a, A::new); } }
	
	@Test
	public void shouldAcceptBetweenInclusive()
	{
		new A($(2));
		new A($(5));
	}

	@Validate(min = 2)
	static final class B extends SingleDecimal<B> { B(BigDecimal a) { super(a, B::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanExclusive()
	{
		new B($(1));
	}

	@Validate(max = 5)
	static final class C extends SingleDecimal<C> { C(BigDecimal a) { super(a, C::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanExclusive()
	{
		new C($(6));
	}

	@Validate(greaterThan = 2, lessThan = 5)
	static final class D extends SingleDecimal<D>
	{
		private D(BigDecimal a) { super(a, D::new); }
		D(String a) { super(a, D::new); }
	}
	
	@Test
	public void shouldAcceptBetweenExclusive()
	{
		new D("3");
		new D("4");
	}

	@Validate(greaterThan = 2)
	static final class E extends SingleDecimal<E> { E(BigDecimal a) { super(a, E::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanInclusive()
	{
		new E($(2));
	}

	@Validate(lessThan = 5)
	static final class F extends SingleDecimal<F> { F(BigDecimal a) { super(a, F::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanInclusive()
	{
		new F($(5));
	}

	@Adjust(floor = 2, ceiling = 5)
	static final class G extends SingleDecimal<G>
	{
		private G(BigDecimal a) { super(a, G::new); }
		G(double a) { super(a, G::new); }
	}
	
	@Test
	public void shouldPassThroughValueWithinRange()
	{
		assertThat(new G(3.0).raw, is($(3.0)));
	}

	@Adjust(floor = 2)
	static final class H extends SingleDecimal<H> { H(BigDecimal a) { super(a, H::new); } }
	
	@Test
	public void shouldAdjustValueBelowFloor()
	{
		assertThat(new H($(1)).raw, is($(2)));
	}

	@Adjust(ceiling = 5)
	static final class I extends SingleDecimal<I> { I(BigDecimal a) { super(a, I::new); } }
	
	@Test
	public void shouldAdjustValueAboveCeiling()
	{
		assertThat(new I($(6)).raw, is($(5)));
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
