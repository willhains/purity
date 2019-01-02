package com.willhains.purity;

import java.text.DecimalFormat;

/**
 * A {@link Single} wrapping a numeric value.
 *
 * @author willhains
 * @param <This>
 */
public @Value interface SingleNumber<This extends SingleNumber<This>> extends SingleComparable<This>
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
}
