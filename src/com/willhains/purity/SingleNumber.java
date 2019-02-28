package com.willhains.purity;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * A {@link Single} wrapping a numeric value.
 *
 * @author willhains
 * @param <This>
 */
public @Pure interface SingleNumber<This extends SingleNumber<This>> extends SingleComparable<This>
{
	Number asNumber();
	default String format(final DecimalFormat format) { return format.format(asNumber()); }
	default String format(final String pattern) { return new DecimalFormat(pattern).format(asNumber()); }
	
	boolean isZero();
	default boolean isNonZero() { return !isZero(); }
	boolean isPositive();
	boolean isNegative();
	int compareToNumber(Number number);
	
	default boolean isGreaterThan(final Number number) { return compareToNumber(number) > 0; }
	default boolean isGreaterThanOrEqualTo(final Number number) { return compareToNumber(number) >= 0; }
	default boolean isLessThan(final Number number) { return compareToNumber(number) < 0; }
	default boolean isLessThanOrEqualTo(final Number number) { return compareToNumber(number) <= 0; }
	
	This plus(Number number);
	This minus(Number number);
	This multiplyBy(Number number);
	This divideBy(Number number);
	
	/**
	 * Convert a value into a {@link BigDecimal} via its {@link Object#toString()} value.
	 * The name of this function is a reminder to programmers to never use floating-point types for money.
	 *
	 * @param x any numeric object or primitive value.
	 */
	static BigDecimal $(final Object x)
	{
		return x instanceof BigDecimal ? (BigDecimal)x : new BigDecimal(String.valueOf(x));
	}
	
	default This plus(final String number) { return plus($(number)); }
	default This minus(final String number) { return minus($(number)); }
	default This multiplyBy(final String number) { return multiplyBy($(number)); }
	default This divideBy(final String number) { return divideBy($(number)); }
}
