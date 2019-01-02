package com.willhains.purity;

import java.math.BigDecimal;
import java.util.function.Function;

import static com.willhains.purity.Rule.validOnlyIf;
import static com.willhains.purity.Rule.validUnless;
import static com.willhains.purity.SingleNumber.$;
import static java.math.RoundingMode.*;

/**
 * A value type wrapping a {@link BigDecimal}.
 *
 * @author willhains
 * @param <This> Self-reference to the subclass type itself.
 */
public abstract @Value class SingleDecimal<This extends SingleDecimal<This>>
	extends Single<BigDecimal, This>
	implements SingleNumber<This>
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
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleDecimal(
		final BigDecimal rawValue,
		final Function<? super BigDecimal, ? extends This> constructor,
		final Rule<BigDecimal> rules)
	{
		super(rawValue, constructor, rules);
	}
	
	protected SingleDecimal(
		final double rawValue,
		final Function<? super BigDecimal, ? extends This> constructor,
		final Rule<BigDecimal> rules)
	{
		this(new BigDecimal(rawValue), constructor, rules);
	}
	
	protected SingleDecimal(
		final String rawValue,
		final Function<? super BigDecimal, ? extends This> constructor,
		final Rule<BigDecimal> rules)
	{
		this(new BigDecimal(rawValue), constructor, rules);
	}
	
	@Override public String toString() { return raw.toPlainString(); }
	
	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	public static Rule<BigDecimal> min(final BigDecimal minValue)
	{
		return validUnless(raw -> raw.compareTo(minValue) < 0, raw -> raw + " < " + minValue);
	}
	
	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	public static Rule<BigDecimal> max(final BigDecimal maxValue)
	{
		return validUnless(raw -> raw.compareTo(maxValue) > 0, raw -> raw + " > " + maxValue);
	}
	
	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	public static Rule<BigDecimal> greaterThan(final BigDecimal lowerBound)
	{
		return validOnlyIf(raw -> raw.compareTo(lowerBound) > 0, raw -> raw + " <= " + lowerBound);
	}
	
	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	public static Rule<BigDecimal> lessThan(final BigDecimal upperBound)
	{
		return validOnlyIf(raw -> raw.compareTo(upperBound) < 0, raw -> raw + " >= " + upperBound);
	}
	
	/** Generate rule to normalise the raw double value to a minimum floor value. */
	public static Rule<BigDecimal> floor(final BigDecimal minValue) { return raw -> raw.max(minValue); }
	
	/** Generate rule to normalise the raw double value to a maximum ceiling value. */
	public static Rule<BigDecimal> ceiling(final BigDecimal maxValue) { return raw -> raw.min(maxValue); }
	
	@Override public Number asNumber() { return raw; }
	
	@Override public final int compareTo(final This that) { return this.raw.compareTo(that.raw); }
	@Override public final int compareToNumber(final Number number) { return this.raw.compareTo($(number)); }
	
	@Override public boolean isZero() { return raw.compareTo(BigDecimal.ZERO) == 0; }
	@Override public boolean isPositive() { return raw.compareTo(BigDecimal.ZERO) > 0; }
	@Override public boolean isNegative() { return raw.compareTo(BigDecimal.ZERO) < 0; }
	
	@Override public final This plus(final Number number) { return map(d -> d.add($(number))); }
	@Override public final This minus(final Number number) { return map(d -> d.subtract($(number))); }
	@Override public final This multiplyBy(final Number number) { return map(d -> d.multiply($(number))); }
	@Override public final This divideBy(final Number number) { return map(d -> d.divide($(number))); }
	
	public final This round() { return map(d -> d.setScale(0, HALF_UP)); }
	public final This roundUp() { return map(d -> d.setScale(0, CEILING)); }
	public final This roundDown() { return map(d -> d.setScale(0, FLOOR)); }
	public final This roundToPrecision(final int decimals) { return map(d -> d.setScale(decimals, HALF_UP)); }
}
