package com.willhains.purity;

import java.util.Optional;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.willhains.purity.LongRule.validOnlyIf;
import static com.willhains.purity.LongRule.validUnless;
import static java.util.Objects.requireNonNull;

/** A primitive `long` version of {@link Single}. */
public abstract @Value class SingleLong<This extends SingleLong<This>> implements SingleComparable<This>
{
	// The single-argument constructor of the subclass
	private final LongFunction<? extends This> _constructor;
	
	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	public final long raw;
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleLong(final long rawValue, final LongFunction<? extends This> constructor)
	{
		raw = requireNonNull(rawValue);
		_constructor = requireNonNull(constructor);
	}
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 * @param rules Validation and data normalisation rules for the raw underlying value.
	 */
	protected SingleLong(final long rawValue, final LongFunction<? extends This> constructor, final LongRule rules)
	{
		this(rules.apply(rawValue), constructor);
	}
	
	@Override public final int hashCode() { return Long.hashCode(raw); }
	@Override public String toString() { return Long.toString(raw); }
	
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
	public static LongRule min(final long minValue)
	{
		return validUnless(raw -> raw < minValue, raw -> raw + " < " + minValue);
	}
	
	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	public static LongRule max(final long maxValue)
	{
		return validUnless(raw -> raw > maxValue, raw -> raw + " > " + maxValue);
	}
	
	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	public static LongRule greaterThan(final long lowerBound)
	{
		return validOnlyIf(raw -> raw > lowerBound, raw -> raw + " <= " + lowerBound);
	}
	
	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	public static LongRule lessThan(final long upperBound)
	{
		return validOnlyIf(raw -> raw < upperBound, raw -> raw + " >= " + upperBound);
	}
	
	/** Generate rule to normalise the raw long value to a minimum floor value. */
	public static LongRule floor(final long minValue) { return raw -> Math.max(raw, minValue); }
	
	/** Generate rule to normalise the raw long value to a maximum ceiling value. */
	public static LongRule ceiling(final long maxValue) { return raw -> Math.min(raw, maxValue); }
	
	@Override public final int compareTo(final This that) { return Long.compare(this.raw, that.raw); }
	
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
	public final boolean is(final LongPredicate condition) { return condition.test(raw); }
	
	/** Reverse of {@link #is(LongPredicate)}. */
	public final boolean isNot(final LongPredicate condition) { return !is(condition); }
	
	/**
	 * Test the raw value by {@code condition}.
	 *
	 * @return an {@link Optional} containing this instance if the condition is met; empty otherwise.
	 * @see #is(LongPredicate)
	 */
	public final Optional<This> filter(final LongPredicate condition)
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
	public final This map(final LongUnaryOperator mapper)
	{
		final long mapped = mapper.applyAsLong(raw);
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
	public final This flatMap(final LongFunction<? extends This> mapper) { return mapper.apply(raw); }
}
