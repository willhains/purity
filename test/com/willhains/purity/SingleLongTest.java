package com.willhains.purity;

import com.willhains.purity.annotations.*;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SingleLongTest
{
	public static final @Pure class Count extends SingleLong<Count>
	{
		public Count(final long rawValue) { super(rawValue, Count::new); }
	}
	
	@Test
	public void shouldReturnRawValueAfterConstruction()
	{
		assertThat(new Count(173L).raw(), equalTo(173L));
	}
	
	@Test
	public void shouldAlwaysBeUnequalToNull()
	{
		final Count x = new Count(123L);
		assertFalse(x.equals(null));
	}
	
	static final class Count2 extends SingleLong<Count2> { public Count2(long x) { super(x, Count2::new); } }
	
	@Test
	public void shouldAlwaysBeUnequalToDifferentClass()
	{
		final Count x = new Count(123L);
		final Count2 y = new Count2(123L);
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
		final Count x = new Count(123L);
		assertTrue(x.equals(x));
	}
	
	@Test
	public void shouldBeSymmetric()
	{
		final Count x = new Count(100L);
		final Count y = new Count(100L);
		assertTrue(x.equals(y));
		assertTrue(y.equals(x));
		final Count z = new Count(101L);
		assertFalse(x.equals(z));
		assertFalse(z.equals(x));
	}
	
	@Test
	public void shouldBeTransitive()
	{
		final Count x = new Count(100L);
		final Count y = new Count(100L);
		final Count z = new Count(100L);
		assertTrue(x.equals(y));
		assertTrue(y.equals(z));
		assertTrue(x.equals(z));
		final Count w = new Count(101L);
		assertFalse(w.equals(y));
		assertFalse(w.equals(z));
	}
	
	@Test
	public void shouldBeConsistent()
	{
		final Count x = new Count(100L);
		final Count y = new Count(100L);
		final Count z = new Count(101L);
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
		final long xHash1 = x.hashCode();
		final long xHash2 = x.hashCode();
		assertThat(xHash1, equalTo(xHash2));
	}
	
	@Test
	public void shouldHaveSameHashCode()
	{
		final Count x = new Count(100L);
		final Count y = new Count(100L);
		assertTrue(x.equals(y));
		final long xHash = x.hashCode();
		final long yHash = y.hashCode();
		assertThat(xHash, equalTo(yHash));
	}
	
	@Test
	public void shouldGenerateSameStringAsUnderlying()
	{
		final Count x = new Count(100L);
		assertThat(x.toString(), equalTo("100"));
	}

	@Validate(min = 2, max = 5)
	static final class A extends SingleLong<A> { A(long a) { super(a, A::new); } }
	
	@Test
	public void shouldAcceptBetweenInclusive()
	{
		new A(2L);
		new A(5L);
	}

	@Validate(min = 2)
	static final class B extends SingleLong<B> { B(long a) { super(a, B::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanExclusive()
	{
		new B(1L);
	}

	@Validate(max = 5)
	static final class C extends SingleLong<C> { C(long a) { super(a, C::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanExclusive() { new C(6L); }

	@Validate(greaterThan = 2, lessThan = 5)
	static final class D extends SingleLong<D> { D(long a) { super(a, D::new); } }
	
	@Test
	public void shouldAcceptBetweenExclusive()
	{
		new D(3L);
		new D(4L);
	}

	@Validate(greaterThan = 2L)
	static final class E extends SingleLong<E> { E(long a) { super(a, E::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapLessThanInclusive()
	{
		new E(2L);
	}

	@Validate(lessThan = 5L)
	static final class F extends SingleLong<F> { F(long a) { super(a, F::new); } }
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldTrapGreaterThanInclusive()
	{
		new F(5L);
	}

	@Adjust(floor = 2, ceiling = 5)
	static final class G extends SingleLong<G> { G(long a) { super(a, G::new); } }
	
	@Test
	public void shouldPassThroughValueWithinRange()
	{
		assertThat(new G(3L).raw(), is(3L));
	}

	@Adjust(floor = 2)
	static final class H extends SingleLong<H> { H(long a) { super(a, H::new); } }
	
	@Test
	public void shouldAdjustValueBelowFloor()
	{
		assertThat(new H(1L).raw(), is(2L));
	}

	@Adjust(ceiling = 5)
	static final class I extends SingleLong<I> { I(long a) { super(a, I::new); } }
	
	@Test
	public void shouldAdjustValueAboveCeiling()
	{
		assertThat(new I(6L).raw(), is(5L));
	}
	
	@Test
	public void shouldTestRawValue()
	{
		final Count x = new Count(100L);
		assertTrue(x.is(i -> i < 1000L));
		assertFalse(x.is(i -> i > 1000L));
	}
	
	@Test
	public void shouldTestRawValueAndNegate()
	{
		final Count x = new Count(100L);
		assertFalse(x.isNot(i -> i < 1000L));
		assertTrue(x.isNot(i -> i > 1000L));
	}
	
	@Test
	public void shouldPassFilterOnMatchingCondition()
	{
		final Count x = new Count(100L);
		final Optional<Count> opX = x.filter($ -> $ > 50L);
		assertThat(opX.get(), is(x));
	}
	
	@Test
	public void shouldFailFilterOnNonMatchingCondition()
	{
		final Count x = new Count(100L);
		final Optional<Count> opX = x.filter($ -> $ > 1000L);
		assertFalse(opX.isPresent());
	}
	
	@Test
	public void shouldMapToNewValue()
	{
		final Count x = new Count(100L);
		final Count y = x.map(f -> f + 1L);
		assertThat(y.raw(), equalTo(101L));
	}
	
	@Test
	public void shouldMapToSameValue()
	{
		final Count x = new Count(100L);
		final Count y = x.map(f -> f + 0L);
		assertEquals(x, y);
	}
	
	@Test
	public void shouldFlatMapToNewValue()
	{
		final Count x = new Count(100L);
		final Count y = x.flatMap(f -> new Count(f + 1L));
		assertThat(y.raw(), equalTo(101L));
	}

	static final class J extends SingleLong<J> { J(long a) { super(a, J::new); } }
	
	@Test
	public void customRules()
	{
		new J(2L);
	}
	
	@Test
	public void shouldCompareLarger()
	{
		final Count x = new Count(100L);
		final Count y = new Count(50L);
		assertTrue(x.compareTo(y) > 0);
		assertTrue(x.compareToNumber(y.raw()) > 0);
	}
	
	@Test
	public void shouldCompareSmaller()
	{
		final Count x = new Count(50L);
		final Count y = new Count(100L);
		assertTrue(x.compareTo(y) < 0);
		assertTrue(x.compareToNumber(y.raw()) < 0);
	}
	
	@Test
	public void shouldCompareEqual()
	{
		final Count x = new Count(100L);
		final Count y = new Count(100L);
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
	
	static final @Pure class Id extends SingleLong<Id> { Id(long id) { super(id, Id::new); } }
	
	@Test
	public void shouldIdentifyNegative()
	{
		final Id x = new Id(-1);
		assertFalse(x.isZero());
		assertTrue(x.isNonZero());
		assertFalse(x.isPositive());
		assertTrue(x.isNegative());
	}
	
	@Test public void shouldAdd() { assertThat(new Count(5).plus(7).raw(), is(12L)); }
	@Test public void shouldSubtract() { assertThat(new Count(5).minus(3).raw(), is(2L)); }
	@Test public void shouldMultiply() { assertThat(new Count(5).multiplyBy(3).raw(), is(15L)); }
	@Test public void shouldDivide() { assertThat(new Count(5).divideBy(2).raw(), is(2L)); }
}
