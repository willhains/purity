package com.willhains.purity;

import org.junit.Test;

import java.util.Optional;

import static com.willhains.purity.DoubleRule.validUnless;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/** @author willhains */
public class SingleDoubleTest
{
	public static final @Pure class Height extends SingleDouble<Height>
	{
		public Height(final double rawValue) { super(rawValue, Height::new); }
	}
	
	@Test
	public void shouldReturnRawValueAfterConstruction()
	{
		assertThat(new Height(17.3).raw(), equalTo(17.3));
	}
	
	@Test
	public void shouldRepresentRawAsNumber()
	{
		final Height x = new Height(12.3d);
		assertThat(x.asNumber(), is(x.raw()));
	}
	
	@Test
	public void shouldAlwaysBeUnequalToNull()
	{
		final Height x = new Height(12.3);
		assertFalse(x.equals(null));
	}
	
	static final class Height2 extends SingleDouble<Height2> { public Height2(double x) { super(x, Height2::new); } }
	
	@Test
	public void shouldAlwaysBeUnequalToDifferentClass()
	{
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
	
	@Validate(allowInfinity = false, allowNaN = false) // Actually, these are the defaults
	static final class A extends SingleDouble<A> { A(double a) { super(a, A::new); } }
	
	@Test
	public void shouldAcceptRealNumber()
	{
		new A(1.0);
		new A(Double.MIN_VALUE);
		new A(Double.MAX_VALUE);
	}

	@Validate
	static final class B extends SingleDouble<B> { B(double a) { super(a, B::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapNaN()
	{
		new B(Double.NaN);
	}

	@Validate
	static final class C extends SingleDouble<C> { C(double a) { super(a, C::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapNegativeInfinity()
	{
		new C(Double.NEGATIVE_INFINITY);
	}

	@Validate
	static final class D extends SingleDouble<D> { D(double a) { super(a, D::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapPositiveInfinity()
	{
		new D(Double.POSITIVE_INFINITY);
	}
	
	@Validate(min = 2.0, max = 5.0)
	static final class E extends SingleDouble<E> { E(double a) { super(a, E::new); } }
	
	@Test
	public void shouldAcceptBetweenInclusive()
	{
		new E(2.0);
		new E(5.0);
	}
	
	@Validate(min = 2)
	static final class F extends SingleDouble<F> { F(double a) { super(a, F::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanExclusive()
	{
		new F(1.0);
	}
	
	@Validate(max = 5)
	static final class G extends SingleDouble<G> { G(double a) { super(a, G::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanExclusive()
	{
		new G(5.00000001);
	}
	
	@Validate(greaterThan = 2, lessThan = 5)
	static final class H extends SingleDouble<H> { H(double a) { super(a, H::new); } }
	
	@Test
	public void shouldAcceptBetweenExclusive()
	{
		new H(3.0);
		new H(4.0);
	}
	
	@Validate(greaterThan = 2.0)
	static final class I extends SingleDouble<I> { I(double a) { super(a, I::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanInclusive()
	{
		new I(2.0);
	}
	
	@Validate(lessThan = 5)
	static final class J extends SingleDouble<J> { J(double a) { super(a, J::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanInclusive()
	{
		new J(5.0);
	}
	
	@Adjust(floor = 2.0, ceiling = 5.0)
	static final class K extends SingleDouble<K> { K(double a) { super(a, K::new); } }
	
	@Test
	public void shouldPassThroughValueWithinRange()
	{
		assertThat(new K(3.0).raw(), is(3.0));
	}

	@Adjust(floor = 2.0)
	static final class L extends SingleDouble<L> { L(double a) { super(a, L::new); } }
	
	@Test
	public void shouldAdjustValueBelowFloor()
	{
		assertThat(new L(1.0).raw(), is(2.0));
	}
	
	@Adjust(ceiling = 5.0)
	static final class M extends SingleDouble<M>
	{
		M(double a) { super(a, M::new); }
	}
	
	@Test
	public void shouldAdjustValueAboveCeiling()
	{
		assertThat(new M(6.0).raw(), is(5.0));
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
		assertThat(y.raw(), equalTo(10.1));
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
		assertThat(y.raw(), equalTo(10.1));
	}
	
	static final class N extends SingleDouble<N>
	{
		static final DoubleRule EVEN = validUnless(raw -> raw % 2 > 0, "Must be even");
		N(double a) { super(a, N::new); }
	}
	
	@Test
	public void customRules()
	{
		new N(2d);
	}
	
	@Test
	public void shouldCompareLarger()
	{
		final Height x = new Height(10.0);
		final Height y = new Height(5.0);
		assertTrue(x.compareTo(y) > 0);
		assertTrue(x.compareToNumber(y.raw()) > 0);
	}
	
	@Test
	public void shouldCompareSmaller()
	{
		final Height x = new Height(5.0);
		final Height y = new Height(10.0);
		assertTrue(x.compareTo(y) < 0);
		assertTrue(x.compareToNumber(y.raw()) < 0);
	}
	
	@Test
	public void shouldCompareEqual()
	{
		final Height x = new Height(10.0);
		final Height y = new Height(10.0);
		assertEquals(x, y);
		assertTrue(x.compareTo(y) == 0);
		assertTrue(x.compareToNumber(y.raw()) == 0);
	}
	
	@Test
	public void shouldIdentifyZero()
	{
		final Height x = new Height(0.0);
		assertTrue(x.isZero());
		assertFalse(x.isNonZero());
		assertFalse(x.isPositive());
		assertFalse(x.isNegative());
	}
	
	@Test
	public void shouldIdentifyPositive()
	{
		final Height x = new Height(0.1);
		assertFalse(x.isZero());
		assertTrue(x.isNonZero());
		assertTrue(x.isPositive());
		assertFalse(x.isNegative());
	}
	
	static final @Pure class Factor extends SingleDouble<Factor> { Factor(double factor) { super(factor, Factor::new); } }
	
	@Test
	public void shouldIdentifyNegative()
	{
		final Factor x = new Factor(-0.1);
		assertFalse(x.isZero());
		assertTrue(x.isNonZero());
		assertFalse(x.isPositive());
		assertTrue(x.isNegative());
	}
	
	@Test public void shouldAdd() { assertThat(new Height(12.3).plus(0.7).raw(), is(13.0)); }
	@Test public void shouldSubtract() { assertThat(new Height(12.3).minus(0.3).raw(), is(12.0)); }
	@Test public void shouldMultiply() { assertThat(new Height(12.3).multiplyBy(10).raw(), is(123.0)); }
	@Test public void shouldDivide() { assertThat(new Height(12.8).divideBy(2).raw(), is(6.4)); }
	
	@Test
	public void shouldRoundHalfUp()
	{
		assertThat(new Height(14.5).round().raw(), is(15.0));
		assertThat(new Height(14.4).round().raw(), is(14.0));
	}
	
	@Test
	public void shouldRoundDown()
	{
		assertThat(new Height(14.9).roundDown().raw(), is(14.0));
	}
	
	@Test
	public void shouldRoundUp()
	{
		assertThat(new Height(14.1).roundUp().raw(), is(15.0));
	}
}
