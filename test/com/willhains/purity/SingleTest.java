package com.willhains.purity;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/** @author willhains */
public class SingleTest
{
	public static final class Height extends Single<Float, Height>
	{
		public Height(final Float rawValue) { super(rawValue, Height::new); }
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldRejectNullUnderlyingValue() { new Height(null); }
	
	@Test
	public void shouldAllowCustomValidations()
	{
		try { new Height(-1f); }
		catch(final IllegalArgumentException e) { assertThat(e.getMessage(), is("-1.0 < 0")); }
		
		try { new Height(Float.NaN); }
		catch(final IllegalArgumentException e) { assertThat(e.getMessage(), is("Not a number")); }
		
		try { new Height(Float.POSITIVE_INFINITY); }
		catch(final IllegalArgumentException e) { assertThat(e.getMessage(), is("Must be finite")); }
	}
	
	@Test
	public void shouldReturnRawValueAfterConstruction()
	{
		assertThat(new Height(173f).raw(), equalTo(173f));
	}
	
	@Test
	public void shouldAlwaysBeUnequalToNull()
	{
		final Height x = new Height(123f);
		assertFalse(x.equals(null));
	}
	
	static final class Height2 extends Single<Float, Height2> { public Height2(Float x) { super(x, Height2::new); } }
	
	@Test
	public void shouldAlwaysBeUnequalToDifferentClass()
	{
		final Height x = new Height(123f);
		final Height2 y = new Height2(123f);
		assertFalse(x.equals(y));
	}
	
	@Test
	public void shouldAlwaysBeEqualWithSameRaw()
	{
		final Float f = 123f;
		final Height x = new Height(f);
		final Height y = new Height(f);
		assertThat(x.raw(), is(sameInstance(y.raw())));
		assertTrue(x.equals(y));
	}
	
	@Test
	public void shouldBeReflexive()
	{
		final Height x = new Height(123f);
		assertTrue(x.equals(x));
	}
	
	@Test
	public void shouldBeSymmetric()
	{
		final Height x = new Height(100f);
		final Height y = new Height(100f);
		assertTrue(x.equals(y));
		assertTrue(y.equals(x));
		final Height z = new Height(101f);
		assertFalse(x.equals(z));
		assertFalse(z.equals(x));
	}
	
	@Test
	public void shouldBeTransitive()
	{
		final Height x = new Height(100f);
		final Height y = new Height(100f);
		final Height z = new Height(100f);
		assertTrue(x.equals(y));
		assertTrue(y.equals(z));
		assertTrue(x.equals(z));
		final Height w = new Height(101f);
		assertFalse(w.equals(y));
		assertFalse(w.equals(z));
	}
	
	@Test
	public void shouldBeConsistent()
	{
		final Height x = new Height(100f);
		final Height y = new Height(100f);
		final Height z = new Height(101f);
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
		final Height x = new Height(100f);
		final Height y = new Height(100f);
		assertTrue(x.equals(y));
		final int xHash = x.hashCode();
		final int yHash = y.hashCode();
		assertThat(xHash, equalTo(yHash));
	}
	
	@Test
	public void shouldGenerateSameStringAsUnderlying()
	{
		final Height x = new Height(100f);
		assertThat(x.toString(), equalTo(x.raw().toString()));
	}
	
	static final @Pure class A1 extends Single<String[], A1> { A1(final String[] a) { super(a, A1::new); } }
	static final @Pure class A2 extends Single<int[], A2> { A2(final int[] a) { super(a, A2::new); } }
	static final @Pure class A3 extends Single<byte[], A3> { A3(final byte[] a) { super(a, A3::new); } }
	static final @Pure class A4 extends Single<boolean[], A4> { A4(final boolean[] a) { super(a, A4::new); } }
	static final @Pure class A5 extends Single<long[], A5> { A5(final long[] a) { super(a, A5::new); } }
	static final @Pure class A6 extends Single<double[], A6> { A6(final double[] a) { super(a, A6::new); } }
	static final @Pure class A7 extends Single<float[], A7> { A7(final float[] a) { super(a, A7::new); } }
	static final @Pure class A8 extends Single<char[], A8> { A8(final char[] a) { super(a, A8::new); } }
	static final @Pure class A9 extends Single<short[], A9> { A9(final short[] a) { super(a, A9::new); } }
	
	@Test
	public void shouldBeEqualIfArrayContentsAreEqual()
	{
		assertTrue(new A1(new String[] {"a","b","c"}).equals(new A1(new String[] {"a","b","c"})));
		assertTrue(new A2(new int[] {1, 2, 3}).equals(new A2(new int[] {1, 2, 3})));
		assertTrue(new A3(new byte[] {1, 2, 3}).equals(new A3(new byte[] {1, 2, 3})));
		assertTrue(new A4(new boolean[] {true, false}).equals(new A4(new boolean[] {true, false})));
		assertTrue(new A5(new long[] {1, 2, 3}).equals(new A5(new long[] {1, 2, 3})));
		assertTrue(new A6(new double[] {1, 2, 3}).equals(new A6(new double[] {1, 2, 3})));
		assertTrue(new A7(new float[] {1, 2, 3}).equals(new A7(new float[] {1, 2, 3})));
		assertTrue(new A8(new char[] {'a','b','c'}).equals(new A8(new char[] {'a','b','c'})));
		assertTrue(new A9(new short[] {1, 2, 3}).equals(new A9(new short[] {1, 2, 3})));
	}
	
	@Test
	public void shouldBeUnequalIfArrayContentsAreUnequal()
	{
		assertFalse(new A1(new String[] {"a","b","c"}).equals(new A1(new String[] {"a","b","d"})));
		assertFalse(new A2(new int[] {1, 2, 3}).equals(new A2(new int[] {1, 2, 4})));
		assertFalse(new A3(new byte[] {1, 2, 3}).equals(new A3(new byte[] {1, 2, 4})));
		assertFalse(new A4(new boolean[] {true, false}).equals(new A4(new boolean[] {true, true})));
		assertFalse(new A5(new long[] {1, 2, 3}).equals(new A5(new long[] {1, 2, 4})));
		assertFalse(new A6(new double[] {1, 2, 3}).equals(new A6(new double[] {1, 2, 4})));
		assertFalse(new A7(new float[] {1, 2, 3}).equals(new A7(new float[] {1, 2, 4})));
		assertFalse(new A8(new char[] {'a','b','c'}).equals(new A8(new char[] {'a','b','d'})));
		assertFalse(new A9(new short[] {1, 2, 3}).equals(new A9(new short[] {1, 2, 4})));
	}
	
	@Test
	public void shouldHaveSameHashCodesIfArrayContentsAreSame()
	{
		assertEquals(new A1(new String[] {"a","b","c"}).hashCode(), new A1(new String[] {"a","b","c"}).hashCode());
		assertEquals(new A2(new int[] {1, 2, 3}).hashCode(), new A2(new int[] {1, 2, 3}).hashCode());
		assertEquals(new A3(new byte[] {1, 2, 3}).hashCode(), new A3(new byte[] {1, 2, 3}).hashCode());
		assertEquals(new A4(new boolean[] {true, false}).hashCode(), new A4(new boolean[] {true, false}).hashCode());
		assertEquals(new A5(new long[] {1, 2, 3}).hashCode(), new A5(new long[] {1, 2, 3}).hashCode());
		assertEquals(new A6(new double[] {1, 2, 3}).hashCode(), new A6(new double[] {1, 2, 3}).hashCode());
		assertEquals(new A7(new float[] {1, 2, 3}).hashCode(), new A7(new float[] {1, 2, 3}).hashCode());
		assertEquals(new A8(new char[] {'a','b','c'}).hashCode(), new A8(new char[] {'a','b','c'}).hashCode());
		assertEquals(new A9(new short[] {1, 2, 3}).hashCode(), (new A9(new short[] {1, 2, 3}).hashCode()));
	}
	
	@Test
	public void shouldHaveDifferentHashCodesIfArrayContentsAreDifferent()
	{
		assertNotEquals(new A1(new String[] {"a","b","c"}).hashCode(), new A1(new String[] {"a","b","d"}).hashCode());
		assertNotEquals(new A2(new int[] {1, 2, 3}).hashCode(), new A2(new int[] {1, 2, 4}).hashCode());
		assertNotEquals(new A3(new byte[] {1, 2, 3}).hashCode(), new A3(new byte[] {1, 2, 4}).hashCode());
		assertNotEquals(new A4(new boolean[] {true, false}).hashCode(), new A4(new boolean[] {true, true}).hashCode());
		assertNotEquals(new A5(new long[] {1, 2, 3}).hashCode(), new A5(new long[] {1, 2, 4}).hashCode());
		assertNotEquals(new A6(new double[] {1, 2, 3}).hashCode(), new A6(new double[] {1, 2, 4}).hashCode());
		assertNotEquals(new A7(new float[] {1, 2, 3}).hashCode(), new A7(new float[] {1, 2, 4}).hashCode());
		assertNotEquals(new A8(new char[] {'a','b','c'}).hashCode(), new A8(new char[] {'a','b','d'}).hashCode());
		assertNotEquals(new A9(new short[] {1, 2, 3}).hashCode(), (new A9(new short[] {1, 2, 4}).hashCode()));
	}
	
	@Test
	public void shouldIncludeAllElementsOfArrayInString()
	{
		final String a1 = new A1(new String[]{"a", "b", "c"}).toString();
		final String a2 = new A2(new int[] {1, 2, 3}).toString();
		final String a3 = new A3(new byte[] {1, 2, 3}).toString();
		final String a4 = new A4(new boolean[] {true, false}).toString();
		final String a5 = new A5(new long[] {1, 2, 3}).toString();
		final String a6 = new A6(new double[] {1, 2, 3}).toString();
		final String a7 = new A7(new float[] {1, 2, 3}).toString();
		final String a8 = new A8(new char[] {'a','b','c'}).toString();
		final String a9 = new A9(new short[] {1, 2, 3}).toString();
		
		assertTrue(a1.contains("a")); assertTrue(a1.contains("b")); assertTrue(a1.contains("c"));
		assertTrue(a2.contains("1")); assertTrue(a2.contains("2")); assertTrue(a2.contains("3"));
		assertTrue(a3.contains("1")); assertTrue(a3.contains("2")); assertTrue(a3.contains("3"));
		assertTrue(a4.contains("true")); assertTrue(a4.contains("false"));
		assertTrue(a5.contains("1")); assertTrue(a5.contains("2")); assertTrue(a5.contains("3"));
		assertTrue(a6.contains("1")); assertTrue(a6.contains("2")); assertTrue(a6.contains("3"));
		assertTrue(a7.contains("1")); assertTrue(a7.contains("2")); assertTrue(a7.contains("3"));
		assertTrue(a8.contains("a")); assertTrue(a8.contains("b")); assertTrue(a8.contains("c"));
		assertTrue(a9.contains("1")); assertTrue(a9.contains("2")); assertTrue(a9.contains("3"));
	}
	
	@Test
	public void shouldTestRawValue()
	{
		final Height x = new Height(100f);
		assertTrue(x.is(Float::isFinite));
		assertFalse(x.is(f -> f.isNaN()));
	}
	
	@Test
	public void shouldTestRawValueAndNegate()
	{
		final Height x = new Height(100f);
		assertFalse(x.isNot(Float::isFinite));
		assertTrue(x.isNot(f -> f.isInfinite()));
	}
	
	@Test
	public void shouldPassFilterOnMatchingCondition()
	{
		final Height x = new Height(100f);
		final Optional<Height> opX = x.filter($ -> $ > 50f);
		assertThat(opX.get(), is(x));
	}
	
	@Test
	public void shouldFailFilterOnNonMatchingCondition()
	{
		final Height x = new Height(100f);
		final Optional<Height> opX = x.filter($ -> $ > 1000f);
		assertFalse(opX.isPresent());
	}
	
	@Test
	public void shouldMapToSameValue()
	{
		final Height x = new Height(100f);
		final Height y = x.map(f -> f);
		assertTrue(x.equals(y));
	}
	
	@Test
	public void shouldMapToNewValue()
	{
		final Height x = new Height(100f);
		final Height y = x.map(f -> f + 1);
		assertThat(y.raw(), equalTo(101f));
	}
	
	@Test
	public void shouldFlatMapToNewValue()
	{
		final Height x = new Height(100f);
		final Height y = x.flatMap(f -> new Height(f + 1));
		assertThat(y.raw(), equalTo(101f));
	}
}
