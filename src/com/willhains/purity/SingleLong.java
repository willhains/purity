package com.willhains.purity;

import java.util.Optional;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.*;

/**
 * A primitive `long` version of {@link Single}.
 *
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
 */
@SuppressWarnings("ClassWithTooManyMethods")
public abstract @Pure class SingleLong<This extends SingleLong<This>> implements SingleNumber<This>, LongSupplier
{
	// The single-argument constructor of the subclass
	private final LongFunction<? extends This> _constructor;

	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	private final long _raw;

	/**
	 * Equivalent to {@link #SingleLong(long, LongFunction, boolean) SingleLong(rawValue, constructor, true)}.
	 */
	protected SingleLong(final long rawValue, final LongFunction<? extends This> constructor)
	{
		this(rawValue, constructor, true);
	}

	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 * @param applyRules Whether to apply rules to the raw value.
	 */
	protected SingleLong(final long rawValue, final LongFunction<? extends This> constructor, final boolean applyRules)
	{
		_raw = applyRules ? _rules().applyTo(rawValue) : rawValue;
		_constructor = requireNonNull(constructor);
	}

	private LongRule _rules() { return LongRule.rulesForClass(getClass()); }

	public final long raw() { return _raw; }

	@Override public final long getAsLong() { return raw(); }

	@Override public final int hashCode() { return Long.hashCode(_raw); }

	/**
	 * Override this method to provide custom {@link Object#toString} formatting.
	 * The default passes the call through to {@link Long#toString()}.
	 */
	@SuppressWarnings("DesignForExtension")
	@Override public String toString() { return Long.toString(_raw); }

	@Override
	public final boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final This that = (This) other;
		return this.raw() == that.raw();
	}

	public final boolean equals(final This that)
	{
		if(that == this) return true;
		if(that == null) return false;
		return this.raw() == that.raw();
	}

	@SuppressWarnings("AutoBoxing")
	@Override public final Long asNumber() { return raw(); }

	@Override public final int compareTo(final This that) { return Long.compare(this.raw(), that.raw()); }

	@Override
	public final int compareToNumber(final Number number) { return Long.compare(raw(), number.longValue()); }

	public final int compareToNumber(final long number) { return Long.compare(raw(), number); }

	@Override public final boolean isZero() { return raw() == 0L; }
	@Override public final boolean isPositive() { return raw() > 0L; }
	@Override public final boolean isNegative() { return raw() < 0L; }

	@Override public final This plus(final Number number) { return plus(number.longValue()); }
	@Override public final This minus(final Number number) { return minus(number.longValue()); }
	@Override public final This multiplyBy(final Number number) { return multiplyBy(number.longValue()); }
	@Override public final This divideBy(final Number number) { return divideBy(number.longValue()); }

	public final This plus(final long number) { return map(raw -> raw + number); }
	public final This minus(final long number) { return map(raw -> raw - number); }
	public final This multiplyBy(final long number) { return map(raw -> raw * number); }
	public final This divideBy(final long number) { return map(raw -> raw / number); }

	public final This plus(final LongSupplier number) { return plus(number.getAsLong()); }
	public final This minus(final LongSupplier number) { return minus(number.getAsLong()); }
	public final This multiplyBy(final LongSupplier number) { return multiplyBy(number.getAsLong()); }
	public final This divideBy(final LongSupplier number) { return divideBy(number.getAsLong()); }

	public final boolean isGreaterThan(final LongSupplier number) { return raw() > number.getAsLong(); }
	public final boolean isGreaterThanOrEqualTo(final LongSupplier number) { return raw() >= number.getAsLong(); }
	public final boolean isLessThan(final LongSupplier number) { return raw() < number.getAsLong(); }
	public final boolean isLessThanOrEqualTo(final LongSupplier number) { return raw() <= number.getAsLong(); }

	/**
	 * Test the raw value with {@code condition}.
	 * This method is useful when using {@link Optional#filter} or {@link Stream#filter}.
	 * <pre>
	 * optional.filter(x -&gt; x.is($ -> $ &gt; 0))
	 * </pre>
	 *
	 * @param condition a {@link Predicate} that tests the raw value type.
	 * @return {@code true} if the underlying {@link #raw} value satisfies {@code condition};
	 *    {@code false} otherwise.
	 */
	public final boolean is(final LongPredicate condition) { return condition.test(raw()); }

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
		@SuppressWarnings("unchecked") final This self = (This) this;
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
		final long mapped = mapper.applyAsLong(raw());
		@SuppressWarnings("unchecked") final This self = (This) this;
		if(mapped == raw()) return self;
		return _constructor.apply(mapped);
	}

	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return The value returned by {@code mapper}.
	 */
	public final This flatMap(final LongFunction<? extends This> mapper) { return mapper.apply(raw()); }
}
