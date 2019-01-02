package com.willhains.purity;

import java.math.BigDecimal;
import java.util.function.Function;

import static com.willhains.purity.Rule.validOnlyIf;
import static com.willhains.purity.Rule.validUnless;

/**
 * A value type wrapping a {@link BigDecimal}.
 *
 * @author willhains
 * @param <This> Self-reference to the subclass type itself.
 */
public abstract @Value class SingleDecimal<This extends SingleDecimal<This>>
	extends Single<BigDecimal, This>
	implements SingleComparable<This>
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
	
	/**
	 * Convert a value into a {@link BigDecimal} via its {@link Object#toString()} value.
	 * The name of this function is a reminder to programmers to never use floating-point types for money.
	 *
	 * @param x any numeric object or primitive value.
	 */
	public static BigDecimal $(final Object x)
	{
		return x instanceof BigDecimal ? (BigDecimal)x : new BigDecimal(String.valueOf(x));
	}
	
	@Override public final int compareTo(final This that) { return this.raw.compareTo(that.raw); }
}
