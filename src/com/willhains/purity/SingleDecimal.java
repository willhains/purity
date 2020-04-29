package com.willhains.purity;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.willhains.purity.Rule.validOnlyIf;
import static com.willhains.purity.Rule.validUnless;
import static com.willhains.purity.SingleNumber.$;
import static java.math.RoundingMode.*;

/**
 * A value type wrapping a {@link BigDecimal}.
 *
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
 */
public abstract @Pure class SingleDecimal<This extends SingleDecimal<This>>
	extends Single<BigDecimal, This>
	implements SingleNumber<This>, Supplier<BigDecimal>
{
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleDecimal(final BigDecimal rawValue, final Function<? super BigDecimal, ? extends This> constructor)
	{
		super(rawValue, constructor);
	}
	
	protected SingleDecimal(final double rawValue, final Function<? super BigDecimal, ? extends This> constructor)
	{
		this(new BigDecimal(rawValue), constructor);
	}
	
	protected SingleDecimal(final String rawValue, final Function<? super BigDecimal, ? extends This> constructor)
	{
		this(new BigDecimal(rawValue), constructor);
	}
	
	protected SingleDecimal(
		final double rawValue,
		final Function<? super BigDecimal, ? extends This> constructor,
		final Rule<BigDecimal> rules)
	{
		this(new BigDecimal(rawValue), constructor);
	}
	
	protected SingleDecimal(
		final String rawValue,
		final Function<? super BigDecimal, ? extends This> constructor,
		final Rule<BigDecimal> rules)
	{
		this(new BigDecimal(rawValue), constructor);
	}
	
	public final BigDecimal raw() { return raw; }
	
	@Override public BigDecimal get() { return raw; }
	
	@Override public String toString() { return raw.toPlainString(); }
	
	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	public static Rule<BigDecimal> min(final @Returned BigDecimal minValue)
	{
		return validUnless(raw -> raw.compareTo(minValue) < 0, raw -> raw + " < " + minValue);
	}
	
	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	public static Rule<BigDecimal> max(final @Returned BigDecimal maxValue)
	{
		return validUnless(raw -> raw.compareTo(maxValue) > 0, raw -> raw + " > " + maxValue);
	}
	
	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	public static Rule<BigDecimal> greaterThan(final @Returned BigDecimal lowerBound)
	{
		return validOnlyIf(raw -> raw.compareTo(lowerBound) > 0, raw -> raw + " <= " + lowerBound);
	}
	
	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	public static Rule<BigDecimal> lessThan(final @Returned BigDecimal upperBound)
	{
		return validOnlyIf(raw -> raw.compareTo(upperBound) < 0, raw -> raw + " >= " + upperBound);
	}
	
	/** Generate rule to normalise the raw double value to a minimum floor value. */
	public static Rule<BigDecimal> floor(final BigDecimal minValue) { return raw -> raw.max(minValue); }
	
	/** Generate rule to normalise the raw double value to a maximum ceiling value. */
	public static Rule<BigDecimal> ceiling(final BigDecimal maxValue) { return raw -> raw.min(maxValue); }
	
	@Override public BigDecimal asNumber() { return raw; }
	
	@Override public final int compareTo(final This that) { return this.raw.compareTo(that.raw); }
	@Override public final int compareToNumber(final Number number) { return this.raw.compareTo($(number)); }
	
	@Override public boolean isZero() { return raw.compareTo(BigDecimal.ZERO) == 0; }
	@Override public boolean isPositive() { return raw.compareTo(BigDecimal.ZERO) > 0; }
	@Override public boolean isNegative() { return raw.compareTo(BigDecimal.ZERO) < 0; }
	
	@Override public final This plus(final Number number) { return map(d -> d.add($(number))); }
	@Override public final This minus(final Number number) { return map(d -> d.subtract($(number))); }
	@Override public final This multiplyBy(final Number number) { return map(d -> d.multiply($(number))); }
	@Override public final This divideBy(final Number number) { return map(d -> d.divide($(number), HALF_UP)); }
	
	public final This round() { return map(d -> d.setScale(0, HALF_UP)); }
	public final This roundUp() { return map(d -> d.setScale(0, CEILING)); }
	public final This roundDown() { return map(d -> d.setScale(0, FLOOR)); }
	public final This roundToPrecision(final int decimals) { return map(d -> d.setScale(decimals, HALF_UP)); }
}
