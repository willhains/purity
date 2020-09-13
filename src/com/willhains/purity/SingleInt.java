package com.willhains.purity;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.*;

/**
 * A primitive `int` version of {@link Single}.
 *
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
 */
@SuppressWarnings("ClassWithTooManyMethods")
public abstract @Pure class SingleInt<This extends SingleInt<This>> implements SingleNumber<This>, IntSupplier
{
	// The single-argument constructor of the subclass
	private final IntFunction<? extends This> _constructor;

	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	private final int _raw;

	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleInt(final int rawValue, final IntFunction<? extends This> constructor)
	{
		_raw = IntRule.rulesForClass(getClass()).applyTo(rawValue);
		_constructor = requireNonNull(constructor);
	}

	/** Return the raw underlying value. */
	public final int raw() { return _raw; }

	@Override public final int getAsInt() { return raw(); }

	@Override public final int hashCode() { return Integer.hashCode(raw()); }

	/**
	 * Override this method to provide custom {@link Object#toString} formatting.
	 * The default passes the call through to {@link Double#toString()}.
	 */
	@SuppressWarnings("DesignForExtension")
	@Override public String toString() { return Integer.toString(raw()); }

	@Override
	public final boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final This that = (This) other;
		return raw() == that.raw();
	}

	public final boolean equals(final This that)
	{
		if(that == this) return true;
		if(that == null) return false;
		return raw() == that.raw();
	}

	@SuppressWarnings("AutoBoxing")
	@Override public final Integer asNumber() { return raw(); }

	@Override public final int compareTo(final This that) { return Integer.compare(raw(), that.raw()); }

	@Override public final int compareToNumber(final Number number)
	{
		return Integer.compare(raw(), number.intValue());
	}

	public final int compareToNumber(final int number) { return Integer.compare(raw(), number); }

	@Override public final boolean isZero() { return raw() == 0; }
	@Override public final boolean isPositive() { return raw() > 0; }
	@Override public final boolean isNegative() { return raw() < 0; }

	@Override public final This plus(final Number number) { return plus(number.intValue()); }
	@Override public final This minus(final Number number) { return minus(number.intValue()); }
	@Override public final This multiplyBy(final Number number) { return multiplyBy(number.intValue()); }
	@Override public final This divideBy(final Number number) { return divideBy(number.intValue()); }

	public final This plus(final int number) { return map(raw -> raw + number); }
	public final This minus(final int number) { return map(raw -> raw - number); }
	public final This multiplyBy(final int number) { return map(raw -> raw * number); }
	public final This divideBy(final int number) { return map(raw -> raw / number); }

	public final This plus(final IntSupplier number) { return plus(number.getAsInt()); }
	public final This minus(final IntSupplier number) { return minus(number.getAsInt()); }
	public final This multiplyBy(final IntSupplier number) { return multiplyBy(number.getAsInt()); }
	public final This divideBy(final IntSupplier number) { return divideBy(number.getAsInt()); }

	public final boolean isGreaterThan(final IntSupplier number) { return raw() > number.getAsInt(); }
	public final boolean isGreaterThanOrEqualTo(final IntSupplier number) { return raw() >= number.getAsInt(); }
	public final boolean isLessThan(final IntSupplier number) { return raw() < number.getAsInt(); }
	public final boolean isLessThanOrEqualTo(final IntSupplier number) { return raw() <= number.getAsInt(); }

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
	public final boolean is(final IntPredicate condition) { return condition.test(raw()); }

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
		@SuppressWarnings("unchecked") final This self = (This) this;
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
		final int mapped = mapper.applyAsInt(raw());
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
	public final This flatMap(final IntFunction<? extends This> mapper) { return mapper.apply(raw()); }
}
