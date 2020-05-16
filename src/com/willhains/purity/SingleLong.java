package com.willhains.purity;

import com.willhains.purity.annotations.Pure;

import java.util.Optional;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * A primitive `long` version of {@link Single}.
 *
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
 */
public abstract @Pure class SingleLong<This extends SingleLong<This>> implements SingleNumber<This>, LongSupplier
{
	// The single-argument constructor of the subclass
	private final LongFunction<? extends This> _constructor;
	
	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	protected final long raw;
	
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
	protected SingleLong(final long rawValue, final LongFunction<? extends This>  constructor, final boolean applyRules)
	{
		raw = applyRules ? _rules().applyTo(rawValue) : rawValue;
		_constructor = requireNonNull(constructor);
	}
	
	private LongRule _rules() { return LongRule.rulesForClass(this.getClass()); }
	
	public final long raw() { return raw; }
	
	@Override public long getAsLong() { return raw; }
	
	@Override public final int hashCode() { return Long.hashCode(raw); }
	@Override public String toString() { return Long.toString(raw); }
	
	@Override
	public final boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final This that = (This) other;
		return this.raw == that.raw;
	}
	
	public final boolean equals(final This that)
	{
		if(that == this) return true;
		if(that == null) return false;
		return this.raw == that.raw;
	}
	
	@Override public Long asNumber() { return raw; }
	
	@Override public final int compareTo(final This that) { return Long.compare(this.raw, that.raw); }
	
	@Override
	public final int compareToNumber(final Number number)
	{
		return Long.compare(this.raw, number.longValue());
	}
	
	public final int compareToNumber(final long number)
	{
		return Long.compare(this.raw, number);
	}
	
	@Override public boolean isZero() { return raw == 0L; }
	@Override public boolean isPositive() { return raw > 0L; }
	@Override public boolean isNegative() { return raw < 0L; }
	
	@Override public final This plus(final Number number) { return plus(number.longValue()); }
	@Override public final This minus(final Number number) { return minus(number.longValue()); }
	@Override public final This multiplyBy(final Number number) { return multiplyBy(number.longValue()); }
	@Override public final This divideBy(final Number number) { return divideBy(number.longValue()); }
	
	public final This plus(final long number) { return map($ -> $ + number); }
	public final This minus(final long number) { return map($ -> $ - number); }
	public final This multiplyBy(final long number) { return map($ -> $ * number); }
	public final This divideBy(final long number) { return map($ -> $ / number); }
	
	public final This plus(final LongSupplier number) { return plus(number.getAsLong()); }
	public final This minus(final LongSupplier number) { return minus(number.getAsLong()); }
	public final This multiplyBy(final LongSupplier number) { return multiplyBy(number.getAsLong()); }
	public final This divideBy(final LongSupplier number) { return divideBy(number.getAsLong()); }
	
	public final boolean isGreaterThan(final LongSupplier number) { return raw > number.getAsLong(); }
	public final boolean isGreaterThanOrEqualTo(final LongSupplier number) { return raw >= number.getAsLong(); }
	public final boolean isLessThan(final LongSupplier number) { return raw < number.getAsLong(); }
	public final boolean isLessThanOrEqualTo(final LongSupplier number) { return raw <= number.getAsLong(); }
	
	/**
	 * Test the raw value with {@code condition}.
	 * This method is useful when using {@link Optional#filter} or {@link Stream#filter}.
	 * <pre>
	 * optional.filter(x -&gt; x.is($ -> $ &gt; 0))
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
