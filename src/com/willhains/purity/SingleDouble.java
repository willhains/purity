package com.willhains.purity;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Objects.*;

/**
 * A primitive `double` version of {@link Single}.
 *
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
 */
public abstract @Pure class SingleDouble<This extends SingleDouble<This>> implements SingleNumber<This>, DoubleSupplier
{
	// The single-argument constructor of the subclass
	private final DoubleFunction<? extends This> _constructor;

	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	private final double _raw;

	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleDouble(final double rawValue, final DoubleFunction<? extends This> constructor)
	{
		_raw = DoubleRule.rulesForClass(this.getClass()).applyTo(rawValue);
		_constructor = requireNonNull(constructor);
	}

	/** Return the raw underlying value. */
	public final double raw() { return _raw; }

	@Override public double getAsDouble() { return _raw; }

	@Override public final int hashCode() { return Double.hashCode(_raw); }
	@Override public String toString() { return Double.toString(_raw); }

	@Override
	public final boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		final This that = (This)other;
		return this.raw() == that.raw();
	}

	public final boolean equals(final This that)
	{
		if(that == this) return true;
		if(that == null) return false;
		return this.raw() == that.raw();
	}

	@Override public Double asNumber() { return _raw; }

	@Override public final int compareTo(final This that) { return Double.compare(this.raw(), that.raw()); }

	@Override
	public final int compareToNumber(final Number number) { return Double.compare(this._raw, number.doubleValue()); }

	public final int compareToNumber(final double number) { return Double.compare(this._raw, number); }

	@Override public boolean isZero() { return _raw == 0d; }
	@Override public boolean isPositive() { return _raw > 0d; }
	@Override public boolean isNegative() { return _raw < 0d; }

	@Override public final This plus(final Number number) { return plus(number.doubleValue()); }
	@Override public final This minus(final Number number) { return minus(number.doubleValue()); }
	@Override public final This multiplyBy(final Number number) { return multiplyBy(number.doubleValue()); }
	@Override public final This divideBy(final Number number) { return divideBy(number.doubleValue()); }

	public final This plus(final double number) { return map($ -> $ + number); }
	public final This minus(final double number) { return map($ -> $ - number); }
	public final This multiplyBy(final double number) { return map($ -> $ * number); }
	public final This divideBy(final double number) { return map($ -> $ / number); }

	public final This plus(final DoubleSupplier number) { return plus(number.getAsDouble()); }
	public final This minus(final DoubleSupplier number) { return minus(number.getAsDouble()); }
	public final This multiplyBy(final DoubleSupplier number) { return multiplyBy(number.getAsDouble()); }
	public final This divideBy(final DoubleSupplier number) { return divideBy(number.getAsDouble()); }

	public final boolean isGreaterThan(final DoubleSupplier number) { return _raw > number.getAsDouble(); }
	public final boolean isGreaterThanOrEqualTo(final DoubleSupplier number) { return _raw >= number.getAsDouble(); }
	public final boolean isLessThan(final DoubleSupplier number) { return _raw < number.getAsDouble(); }
	public final boolean isLessThanOrEqualTo(final DoubleSupplier number) { return _raw <= number.getAsDouble(); }

	public final This round() { return map(Math::round); }
	public final This roundUp() { return map(Math::ceil); }
	public final This roundDown() { return map(Math::floor); }

	/**
	 * Test the raw value with {@code condition}.
	 * This method is useful when using {@link Optional#filter} or {@link Stream#filter}.
	 * <pre>
	 * optional.filter(x -&gt; x.is($ -> $ &gt; 0))
	 * </pre>
	 *
	 * @param condition a {@link Predicate} that tests the raw value type.
	 * @return {@code true} if the underlying {@link #_raw} value satisfies {@code condition};
	 *    {@code false} otherwise.
	 */
	public final boolean is(final DoublePredicate condition) { return condition.test(_raw); }

	/** Reverse of {@link #is(DoublePredicate)}. */
	public final boolean isNot(final DoublePredicate condition) { return !is(condition); }

	/**
	 * Test the raw value by {@code condition}.
	 *
	 * @return an {@link Optional} containing this instance if the condition is met; empty otherwise.
	 * @see #is(DoublePredicate)
	 */
	public final Optional<This> filter(final DoublePredicate condition)
	{
		final This self = (This)this;
		return Optional.of(self).filter(it -> it.is(condition));
	}

	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return A new instance of this type.
	 */
	public final This map(final DoubleUnaryOperator mapper)
	{
		final double mapped = mapper.applyAsDouble(_raw);
		final This self = (This)this;
		if(mapped == _raw) return self;
		return _constructor.apply(mapped);
	}

	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return The value returned by {@code mapper}.
	 */
	public final This flatMap(final DoubleFunction<? extends This> mapper) { return mapper.apply(_raw); }
}
