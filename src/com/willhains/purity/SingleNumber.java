package com.willhains.purity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.function.Supplier;

/**
 * A {@link Single} wrapping a numeric value.
 *
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
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
	
	default boolean isGreaterThan(final Supplier<? extends Number> number) { return isGreaterThan(number.get()); }
	default boolean isGreaterThanOrEqualTo(final Supplier<? extends Number> number) { return isGreaterThanOrEqualTo(number.get()); }
	default boolean isLessThan(final Supplier<? extends Number> number) { return isLessThan(number.get()); }
	default boolean isLessThanOrEqualTo(final Supplier<? extends Number> number) { return isLessThanOrEqualTo(number.get()); }
	
	This plus(Number number);
	This minus(Number number);
	This multiplyBy(Number number);
	This divideBy(Number number);
	
	default This plus(final Supplier<? extends Number> number) { return plus(number.get()); }
	default This minus(final Supplier<? extends Number> number) { return minus(number.get()); }
	default This multiplyBy(final Supplier<? extends Number> number) { return multiplyBy(number.get()); }
	default This divideBy(final Supplier<? extends Number> number) { return divideBy(number.get()); }
	
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

	static BigDecimal $(final double x) { return BigDecimal.valueOf(x).stripTrailingZeros(); }
	static BigDecimal $(final long x) { return BigDecimal.valueOf(x).stripTrailingZeros(); }
	
	default This plus(final String number) { return plus($(number)); }
	default This minus(final String number) { return minus($(number)); }
	default This multiplyBy(final String number) { return multiplyBy($(number)); }
	default This divideBy(final String number) { return divideBy($(number)); }
}
