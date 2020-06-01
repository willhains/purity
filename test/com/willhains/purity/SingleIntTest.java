package com.willhains.purity;

import org.junit.*;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/** @author willhains */
public class SingleIntTest
{
	public static final @Pure class Count extends SingleInt<Count>
	{
		public Count(final int rawValue) { super(rawValue, Count::new); }
	}

	@Test
	public void shouldReturnRawValueAfterConstruction()
	{
		assertThat(new Count(173).raw(), equalTo(173));
	}

	@Test
	public void shouldAlwaysBeUnequalToNull()
	{
		final Count x = new Count(123);
		assertFalse(x.equals(null));
	}

	static final class Count2 extends SingleInt<Count2>
	{
		public Count2(final int x) { super(x, Count2::new); }
	}

	@Test
	public void shouldAlwaysBeUnequalToDifferentClass()
	{
		final Count x = new Count(123);
		final Count2 y = new Count2(123);
		assertFalse(x.equals(y));
	}

	@Test
	public void shouldRepresentRawAsNumber()
	{
		final Count x = new Count(12);
		assertThat(x.asNumber(), is(x.raw()));
	}

	@Test
	public void shouldBeReflexive()
	{
		final Count x = new Count(123);
		assertTrue(x.equals(x));
	}

	@Test
	public void shouldBeSymmetric()
	{
		final Count x = new Count(100);
		final Count y = new Count(100);
		assertTrue(x.equals(y));
		assertTrue(y.equals(x));
		final Count z = new Count(101);
		assertFalse(x.equals(z));
		assertFalse(z.equals(x));
	}

	@Test
	public void shouldBeTransitive()
	{
		final Count x = new Count(100);
		final Count y = new Count(100);
		final Count z = new Count(100);
		assertTrue(x.equals(y));
		assertTrue(y.equals(z));
		assertTrue(x.equals(z));
		final Count w = new Count(101);
		assertFalse(w.equals(y));
		assertFalse(w.equals(z));
	}

	@Test
	public void shouldBeConsistent()
	{
		final Count x = new Count(100);
		final Count y = new Count(100);
		final Count z = new Count(101);
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
		final Count x = new Count(100);
		final Count y = new Count(100);
		assertTrue(x.equals(y));
		final int xHash = x.hashCode();
		final int yHash = y.hashCode();
		assertThat(xHash, equalTo(yHash));
	}

	@Test
	public void shouldGenerateSameStringAsUnderlying()
	{
		final Count x = new Count(100);
		assertThat(x.toString(), equalTo("100"));
	}

	@Validate(min = 2, max = 5)
	static final class A extends SingleInt<A>
	{
		A(final int a) { super(a, A::new); }
	}

	@Test
	public void shouldAcceptBetweenInclusive()
	{
		new A(2);
		new A(5);
	}

	@Validate(min = 2)
	static final class B extends SingleInt<B>
	{
		B(final int a) { super(a, B::new); }
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanExclusive()
	{
		new B(1);
	}

	@Validate(max = 5)
	static final class C extends SingleInt<C>
	{
		C(final int a) { super(a, C::new); }
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanExclusive()
	{
		new C(6);
	}

	@Validate(greaterThan = 2, lessThan = 5)
	static final class D extends SingleInt<D>
	{
		D(final int a) { super(a, D::new); }
	}

	@Test
	public void shouldAcceptBetweenExclusive()
	{
		new D(3);
		new D(4);
	}

	@Validate(greaterThan = 2)
	static final class E extends SingleInt<E>
	{
		E(final int a) { super(a, E::new); }
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanInclusive()
	{
		new E(2);
	}

	@Validate(lessThan = 5)
	static final class F extends SingleInt<F>
	{
		F(final int a) { super(a, F::new); }
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanInclusive()
	{
		new F(5);
	}

	@Adjust(floor = 2, ceiling = 5)
	static final class G extends SingleInt<G>
	{
		G(final int a) { super(a, G::new); }
	}

	@Test
	public void shouldPassThroughValueWithinRange()
	{
		assertThat(new G(3).raw(), is(3));
	}

	@Adjust(floor = 2)
	static final class H extends SingleInt<H>
	{
		H(final int a) { super(a, H::new); }
	}

	@Test
	public void shouldAdjustValueBelowFloor()
	{
		assertThat(new H(1).raw(), is(2));
	}

	@Adjust(ceiling = 5)
	static final class I extends SingleInt<I>
	{
		I(final int a) { super(a, I::new); }
	}

	@Test
	public void shouldAdjustValueAboveCeiling()
	{
		assertThat(new I(6).raw(), is(5));
	}

	@Test
	public void shouldTestRawValue()
	{
		final Count x = new Count(100);
		assertTrue(x.is(i -> i < 1000));
		assertFalse(x.is(i -> i > 1000));
	}

	@Test
	public void shouldTestRawValueAndNegate()
	{
		final Count x = new Count(100);
		assertFalse(x.isNot(i -> i < 1000));
		assertTrue(x.isNot(i -> i > 1000));
	}

	@Test
	public void shouldPassFilterOnMatchingCondition()
	{
		final Count x = new Count(100);
		final Optional<Count> opX = x.filter($ -> $ > 50);
		assertThat(opX.get(), is(x));
	}

	@Test
	public void shouldFailFilterOnNonMatchingCondition()
	{
		final Count x = new Count(100);
		final Optional<Count> opX = x.filter($ -> $ > 1000);
		assertFalse(opX.isPresent());
	}

	@Test
	public void shouldMapToNewValue()
	{
		final Count x = new Count(100);
		final Count y = x.map(f -> f + 1);
		assertThat(y.raw(), equalTo(101));
	}

	@Test
	public void shouldMapToSameValue()
	{
		final Count x = new Count(100);
		final Count y = x.map(f -> f + 0);
		assertEquals(x, y);
	}

	@Test
	public void shouldFlatMapToNewValue()
	{
		final Count x = new Count(100);
		final Count y = x.flatMap(f -> new Count(f + 1));
		assertThat(y.raw(), equalTo(101));
	}

	static final class J extends SingleInt<J>
	{
		J(final int a) { super(a, J::new); }
	}

	@Test
	public void customRules() { new J(2); }

	@Adjust(roundToIncrement = 5)
	static final class K extends SingleInt<K>
	{
		K(final int k) { super(k, K::new); }
	}

	@Test
	public void shouldRoundToIncrementGreaterThanOne()
	{
		final K x = new K(13);
		final K y = new K(12);
		assertThat(x.raw(), is(15));
		assertThat(y.raw(), is(10));
	}

	@Test
	public void shouldCompareLarger()
	{
		final Count x = new Count(100);
		final Count y = new Count(50);
		assertTrue(x.compareTo(y) > 0);
		assertTrue(x.compareToNumber(y.raw()) > 0);
	}

	@Test
	public void shouldCompareSmaller()
	{
		final Count x = new Count(50);
		final Count y = new Count(100);
		assertTrue(x.compareTo(y) < 0);
		assertTrue(x.compareToNumber(y.raw()) < 0);
	}

	@Test
	public void shouldCompareEqual()
	{
		final Count x = new Count(100);
		final Count y = new Count(100);
		assertEquals(x, y);
		assertTrue(x.compareTo(y) == 0);
		assertTrue(x.compareToNumber(y.raw()) == 0);
	}

	@Test
	public void shouldIdentifyZero()
	{
		final Count x = new Count(0);
		assertTrue(x.isZero());
		assertFalse(x.isNonZero());
		assertFalse(x.isPositive());
		assertFalse(x.isNegative());
	}

	@Test
	public void shouldIdentifyPositive()
	{
		final Count x = new Count(1);
		assertFalse(x.isZero());
		assertTrue(x.isNonZero());
		assertTrue(x.isPositive());
		assertFalse(x.isNegative());
	}

	static final @Pure class Id extends SingleInt<Id>
	{
		Id(final int id) { super(id, Id::new); }
	}

	@Test
	public void shouldIdentifyNegative()
	{
		final Id x = new Id(-1);
		assertFalse(x.isZero());
		assertTrue(x.isNonZero());
		assertFalse(x.isPositive());
		assertTrue(x.isNegative());
	}

	@Test public void shouldAdd() { assertThat(new Count(5).plus(7).raw(), is(12)); }
	@Test public void shouldSubtract() { assertThat(new Count(5).minus(3).raw(), is(2)); }
	@Test public void shouldMultiply() { assertThat(new Count(5).multiplyBy(3).raw(), is(15)); }
	@Test public void shouldDivide() { assertThat(new Count(5).divideBy(2).raw(), is(2)); }
}
