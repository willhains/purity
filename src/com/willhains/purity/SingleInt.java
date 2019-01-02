package com.willhains.purity;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.willhains.purity.IntRule.validOnlyIf;
import static com.willhains.purity.IntRule.validUnless;
import static java.util.Objects.requireNonNull;

/** A primitive `int` version of {@link Single}. */
public abstract @Value class SingleInt<This extends SingleInt<This>> implements SingleNumber<This>
{
	// The single-argument constructor of the subclass
	private final IntFunction<? extends This> _constructor;
	
	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	public final int raw;
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleInt(final int rawValue, final IntFunction<? extends This> constructor)
	{
		raw = requireNonNull(rawValue);
		_constructor = requireNonNull(constructor);
	}
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 * @param rules Validation and data normalisation rules for the raw underlying value.
	 */
	protected SingleInt(final int rawValue, final IntFunction<? extends This> constructor, final IntRule rules)
	{
		this(rules.apply(rawValue), constructor);
	}
	
	@Override public final int hashCode() { return Integer.hashCode(raw); }
	@Override public String toString() { return Integer.toString(raw); }
	
	@Override
	public final boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final This that = (This)other;
		return this.raw == that.raw;
	}
	
	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	public static IntRule min(final int minValue)
	{
		return validUnless(raw -> raw < minValue, raw -> raw + " < " + minValue);
	}
	
	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	public static IntRule max(final int maxValue)
	{
		return validUnless(raw -> raw > maxValue, raw -> raw + " > " + maxValue);
	}
	
	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	public static IntRule greaterThan(final int lowerBound)
	{
		return validOnlyIf(raw -> raw > lowerBound, raw -> raw + " <= " + lowerBound);
	}
	
	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	public static IntRule lessThan(final int upperBound)
	{
		return validOnlyIf(raw -> raw < upperBound, raw -> raw + " >= " + upperBound);
	}
	
	/** Generate rule to normalise the raw integer value to a minimum floor value. */
	public static IntRule floor(final int minValue) { return raw -> Math.max(raw, minValue); }
	
	/** Generate rule to normalise the raw integer value to a maximum ceiling value. */
	public static IntRule ceiling(final int maxValue) { return raw -> Math.min(raw, maxValue); }
	
	@Override public Number asNumber() { return raw; }
	
	@Override public final int compareTo(final This that) { return Integer.compare(this.raw, that.raw); }
	
	@Override
	public final int compareToNumber(final Number number)
	{
		return Integer.compare(this.raw, number.intValue());
	}
	
	@Override public boolean isZero() { return raw == 0; }
	@Override public boolean isPositive() { return raw > 0; }
	@Override public boolean isNegative() { return raw < 0; }
	
	@Override public final This plus(final Number number) { return map($ -> $ + number.intValue()); }
	@Override public final This minus(final Number number) { return map($ -> $ - number.intValue()); }
	@Override public final This multiplyBy(final Number number) { return map($ -> $ * number.intValue()); }
	@Override public final This divideBy(final Number number) { return map($ -> $ / number.intValue()); }
	
	/**
	 * Test the raw value with {@code condition}.
	 * This method is useful when using {@link Optional#filter} or {@link Stream#filter}.
	 * <pre>
	 * optional.filter(x -&gt; x.is($ -> $ > 0))
	 * </pre>
	 *
	 * @param condition a {@link Predicate} that tests the raw value type.
	 * @return {@code true} if the underlying {@link #raw} value satisfies {@code condition};
	 *         {@code false} otherwise.
	 */
	public final boolean is(final IntPredicate condition) { return condition.test(raw); }
	
	/** Reverse of {@link #is(IntPredicate)}. */
	public final boolean isNot(final IntPredicate condition) { return !is(condition); }
	
	/**
	 * Test the raw value by {@code condition}.
	 *
	 * @return an {@link Optional} containing this instance if the condition is met; empty otherwise.
	 * @see #is(IntPredicate)
	 */
	public final Optional<This> filter(final IntPredicate condition)
	{
		@SuppressWarnings("unchecked") final This self = (This)this;
		return Optional.of(self).filter(it -> it.is(condition));
	}
	
	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return A new instance of this type.
	 */
	public final This map(final IntUnaryOperator mapper)
	{
		final int mapped = mapper.applyAsInt(raw);
		@SuppressWarnings("unchecked") final This self = (This)this;
		if(mapped == raw) return self;
		return _constructor.apply(mapped);
	}
	
	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return The value returned by {@code mapper}.
	 */
	public final This flatMap(final IntFunction<? extends This> mapper) { return mapper.apply(raw); }
}
