package com.willhains.purity;

import com.willhains.purity.annotations.Pure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Stream;

import static com.willhains.purity.DoubleRule.*;
import static java.util.Objects.requireNonNull;

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
	protected final double raw;
	
	/**
	 * Equivalent to {@link #SingleDouble(double, DoubleFunction, boolean) SingleDouble(rawValue, constructor, true)}.
	 */
	protected SingleDouble(final double rawValue, final DoubleFunction<? extends This> constructor)
	{
		this(rawValue, constructor, true);
	}
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 * @param applyRules Whether to apply rules to the raw value.
	 */
	protected SingleDouble(final double rawValue, final DoubleFunction<? extends This> constructor, boolean applyRules)
	{
		raw = applyRules ? _rules().apply(rawValue) : rawValue;
		_constructor = requireNonNull(constructor);
	}
	
	// Cache of rules of SingleDouble subclasses
	// The map instance itself is never mutated; each update copies and replaces the reference below.
	// The contents come from each subclass's RULES constant, so if an entry is lost due to a race condition,
	// exactly the same value will be regenerated and added to the cache.
	private static Map<Class<? extends SingleDouble<?>>, DoubleRule> _RULES = new HashMap<>();
	
	private DoubleRule _rules()
	{
		// Find a cached rule for This class
		@SuppressWarnings("unchecked") final Class<This> single = (Class<This>)this.getClass();
		@SuppressWarnings("unchecked") final DoubleRule rules = _RULES.get(single);
		if(rules != null) return rules;
		
		// Build a new rule from the DoubleRule constants declared in This class
		final DoubleRule newRule = DoubleRule.combine(DoubleRule.rulesForClass(single));
		
		// Copy and replace the cache with the added rule
		final Map<Class<? extends SingleDouble<?>>, DoubleRule> rulesCache = new HashMap<>(_RULES);
		rulesCache.put(single, newRule);
		_RULES = rulesCache;
		return newRule;
	}
	
	/** Return the raw underlying value. */
	public final double raw() { return raw; }
	
	@Override public double getAsDouble() { return raw; }
	
	@Override public final int hashCode() { return Double.hashCode(raw); }
	@Override public String toString() { return Double.toString(raw); }
	
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
	
	/** Rule to trap non-numbers: NaN, infinity. */
	public static DoubleRule realNumber = combine(
		validUnless(Double::isNaN, "Not a number"),
		validIf(Double::isFinite, "Must be finite"));
	
	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	public static DoubleRule min(final double minValue)
	{
		return validUnless(raw -> raw < minValue, raw -> raw + " < " + minValue);
	}
	
	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	public static DoubleRule max(final double maxValue)
	{
		return validUnless(raw -> raw > maxValue, raw -> raw + " > " + maxValue);
	}
	
	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	public static DoubleRule greaterThan(final double lowerBound)
	{
		return validIf(raw -> raw > lowerBound, raw -> raw + " <= " + lowerBound);
	}
	
	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	public static DoubleRule lessThan(final double upperBound)
	{
		return validIf(raw -> raw < upperBound, raw -> raw + " >= " + upperBound);
	}
	
	/** Generate rule to normalise the raw double value to a minimum floor value. */
	public static DoubleRule floor(final double minValue) { return raw -> Math.max(raw, minValue); }
	
	/** Generate rule to normalise the raw double value to a maximum ceiling value. */
	public static DoubleRule ceiling(final double maxValue) { return raw -> Math.min(raw, maxValue); }
	
	@Override public Double asNumber() { return raw; }
	
	@Override public final int compareTo(final This that) { return Double.compare(this.raw, that.raw); }
	
	@Override
	public final int compareToNumber(final Number number)
	{
		return Double.compare(this.raw, number.doubleValue());
	}
	
	public final int compareToNumber(final double number)
	{
		return Double.compare(this.raw, number);
	}
	
	@Override public boolean isZero() { return raw == 0d; }
	@Override public boolean isPositive() { return raw > 0d; }
	@Override public boolean isNegative() { return raw < 0d; }
	
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
	
	public final boolean isGreaterThan(final DoubleSupplier number) { return raw > number.getAsDouble(); }
	public final boolean isGreaterThanOrEqualTo(final DoubleSupplier number) { return raw >= number.getAsDouble(); }
	public final boolean isLessThan(final DoubleSupplier number) { return raw < number.getAsDouble(); }
	public final boolean isLessThanOrEqualTo(final DoubleSupplier number) { return raw <= number.getAsDouble(); }
	
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
	 * @return {@code true} if the underlying {@link #raw} value satisfies {@code condition};
	 *         {@code false} otherwise.
	 */
	public final boolean is(final DoublePredicate condition) { return condition.test(raw); }
	
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
		@SuppressWarnings("unchecked") final This self = (This)this;
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
		final double mapped = mapper.applyAsDouble(raw);
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
	public final This flatMap(final DoubleFunction<? extends This> mapper) { return mapper.apply(raw); }
}
