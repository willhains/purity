package com.willhains.purity;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static com.willhains.purity.SingleNumber.*;
import static java.math.BigDecimal.*;
import static java.math.RoundingMode.*;
import static java.util.Objects.*;

/**
 * A value type wrapping a {@link BigDecimal}.
 *
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
 */
public abstract @Pure class SingleDecimal<This extends SingleDecimal<This>>
	implements SingleNumber<This>, Supplier<BigDecimal>
{
	// The single-argument constructor of the subclass
	private final Function<? super BigDecimal, ? extends This> _constructor;

	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	private final BigDecimal _raw;

	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleDecimal(final BigDecimal rawValue, final Function<? super BigDecimal, ? extends This> constructor)
	{
		_raw = DecimalRule.rulesForClass(this.getClass()).applyTo(rawValue);
		_constructor = requireNonNull(constructor);
	}

	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleDecimal(final double rawValue, final Function<? super BigDecimal, ? extends This> constructor)
	{
		this($(rawValue), constructor);
	}

	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleDecimal(final String rawValue, final Function<? super BigDecimal, ? extends This> constructor)
	{
		this(new BigDecimal(rawValue), constructor);
	}

	public final BigDecimal raw() { return _raw; }

	@Override public BigDecimal get() { return _raw; }

	@Override public String toString() { return _raw.toPlainString(); }

	@Override public BigDecimal asNumber() { return _raw; }

	@Override public final int compareTo(final This that) { return this.raw().compareTo(that.raw()); }
	@Override public final int compareToNumber(final Number number) { return this._raw.compareTo($(number)); }

	@Override public boolean isZero() { return _raw.compareTo(ZERO) == 0; }
	@Override public boolean isPositive() { return _raw.compareTo(ZERO) > 0; }
	@Override public boolean isNegative() { return _raw.compareTo(ZERO) < 0; }

	@Override public final This plus(final Number number) { return map(d -> d.add($(number))); }
	@Override public final This minus(final Number number) { return map(d -> d.subtract($(number))); }
	@Override public final This multiplyBy(final Number number) { return map(d -> d.multiply($(number))); }
	@Override public final This divideBy(final Number number) { return map(d -> d.divide($(number), HALF_UP)); }

	public final This round() { return map(d -> d.setScale(0, HALF_UP)); }
	public final This roundUp() { return map(d -> d.setScale(0, CEILING)); }
	public final This roundDown() { return map(d -> d.setScale(0, FLOOR)); }
	public final This roundToPrecision(final int decimals) { return map(d -> d.setScale(decimals, HALF_UP)); }

	@Override
	public final int hashCode() { return Single.hashCode(this._raw); }

	@Override
	public final boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		final This that = (This)other;
		return Single.equals(this.raw(), that.raw());
	}

	public final boolean equals(final This that)
	{
		if(that == this) return true;
		if(that == null) return false;
		return Single.equals(this.raw(), that.raw());
	}

	/**
	 * Test the raw value with {@code condition}.
	 * This method is useful when using {@link Optional#filter} or {@link Stream#filter}.
	 * <pre>
	 * optional.filter(x -&gt; x.is(String::isEmpty))
	 * </pre>
	 *
	 * @param condition a {@link Predicate} that tests the raw value type.
	 * @return {@code true} if the underlying {@link #_raw} value satisfies {@code condition};
	 *    {@code false} otherwise.
	 */
	public final boolean is(final Predicate<? super BigDecimal> condition) { return condition.test(raw()); }

	/** Reverse of {@link #is(Predicate)}. */
	public final boolean isNot(final Predicate<? super BigDecimal> condition) { return !is(condition); }

	/**
	 * Test the raw value by {@code condition}.
	 *
	 * @return an {@link Optional} containing this instance if the condition is met; empty otherwise.
	 * @see #is(Predicate)
	 */
	public final Optional<This> filter(final Predicate<? super BigDecimal> condition)
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
	public final This map(final Function<? super BigDecimal, ? extends BigDecimal> mapper)
	{
		final BigDecimal mapped = mapper.apply(raw());
		final This self = (This)this;
		if(mapped.equals(raw())) return self;
		return _constructor.apply(mapped);
	}

	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return The value returned by {@code mapper}.
	 */
	public final This flatMap(final Function<? super BigDecimal, ? extends This> mapper) { return mapper.apply(raw()); }
}
