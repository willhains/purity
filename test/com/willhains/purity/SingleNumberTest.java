package com.willhains.purity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import org.junit.Test;

import static com.willhains.purity.SingleNumber.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/** @author willhains */
public class SingleNumberTest
{
	static final @Pure class Height implements SingleNumber<Height>
	{
		final float raw;
		Height(final float rawValue) { raw = rawValue; }

		@Override public Number asNumber() { return raw; }

		@Override public int compareTo(final Height that) { return Float.compare(raw, that.raw); }
		@Override public int compareToNumber(final Number number) { return Float.compare(raw, number.floatValue()); }

		@Override public boolean isZero() { return raw == 0f; }
		@Override public boolean isPositive() { return raw > 0f; }
		@Override public boolean isNegative() { return raw < 0f; }

		@Override public Height plus(final Number number) { return new Height(raw + number.floatValue()); }
		@Override public Height minus(final Number number) { return new Height(raw - number.floatValue()); }
		@Override public Height multiplyBy(final Number number) { return new Height(raw * number.floatValue()); }
		@Override public Height divideBy(final Number number) { return new Height(raw / number.floatValue()); }
	}

	@Test
	public void shouldFormatAsDecimal()
	{
		final Height x = new Height(12345.6789f);
		assertThat(x.format(new DecimalFormat("#,###.00")), is("12,345.68"));
		assertThat(x.format("#,###.00"), is("12,345.68"));
	}

	@Test
	public void shouldIdentifyZero()
	{
		final Height x = new Height(0.0f);
		assertTrue(x.isZero());
		assertFalse(x.isNonZero());
	}

	@Test
	public void shouldCompareLarger()
	{
		final Height x = new Height(10f);

		assertTrue(x.isGreaterThan(8));
		assertTrue(x.isGreaterThan(8.0d));
		assertTrue(x.isGreaterThan(8.0f));
		assertTrue(x.isGreaterThanOrEqualTo(8));
		assertTrue(x.isGreaterThanOrEqualTo(8.0d));
		assertTrue(x.isGreaterThanOrEqualTo(8.0f));

		assertFalse(x.isGreaterThan(12));
		assertFalse(x.isGreaterThan(12.0d));
		assertFalse(x.isGreaterThan(12.0f));
		assertFalse(x.isGreaterThanOrEqualTo(12));
		assertFalse(x.isGreaterThanOrEqualTo(12.0d));
		assertFalse(x.isGreaterThanOrEqualTo(12.0f));
	}

	@Test
	public void shouldCompareSmaller()
	{
		final Height x = new Height(10f);

		assertTrue(x.isLessThan(12));
		assertTrue(x.isLessThan(12.0d));
		assertTrue(x.isLessThan(12.0f));
		assertTrue(x.isLessThanOrEqualTo(12));
		assertTrue(x.isLessThanOrEqualTo(12.0d));
		assertTrue(x.isLessThanOrEqualTo(12.0f));

		assertFalse(x.isLessThan(8));
		assertFalse(x.isLessThan(8.0d));
		assertFalse(x.isLessThan(8.0f));
		assertFalse(x.isLessThanOrEqualTo(8));
		assertFalse(x.isLessThanOrEqualTo(8.0d));
		assertFalse(x.isLessThanOrEqualTo(8.0f));
	}

	static final class Name extends SingleString<Name>
	{
		Name(final String x) { super(x, Name::new); }
	}

	@Test
	public void shouldConvertAnythingToDecimal()
	{
		assertThat($("12345.67890"), is(new BigDecimal("12345.67890")));
		assertThat($(12.3450), is(new BigDecimal("12.345")));
		assertThat($(6), is(new BigDecimal("6")));
		assertThat($(new Name("12.345")), is(new BigDecimal("12.345")));
	}

	@Test
	public void shouldKeepBigDecimal()
	{
		final BigDecimal x = new BigDecimal("123.4500");
		assertThat($(x), is(sameInstance(x)));
	}

	@Test
	public void shouldConvertStrings()
	{
		final Height x = new Height(10f);
		assertThat(x.plus("1.2").raw, is(11.2f));
		assertThat(x.minus("0.2").raw, is(9.8f));
		assertThat(x.multiplyBy("2.5").raw, is(25f));
		assertThat(x.divideBy("2.5").raw, is(4f));
	}
}
