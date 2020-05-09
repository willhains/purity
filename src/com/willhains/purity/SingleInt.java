package com.willhains.purity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Stream;

import static com.willhains.purity.IntRule.validIf;
import static com.willhains.purity.IntRule.validUnless;
import static java.util.Objects.requireNonNull;

/**
 * A primitive `int` version of {@link Single}.
 *
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
 */
public abstract @Pure class SingleInt<This extends SingleInt<This>> implements SingleNumber<This>, IntSupplier
{
	// The single-argument constructor of the subclass
	private final IntFunction<? extends This> _constructor;
	
	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	protected final int raw;
	
	/**
	 * Equivalent to {@link #SingleInt(int, IntFunction, boolean) SingleInt(rawValue, constructor, true)}.
	 */
	protected SingleInt(final int rawValue, final IntFunction<? extends This> constructor)
	{
		this(rawValue, constructor, true);
	}
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 * @param applyRules Whether to apply rules to the raw value.
	 */
	protected SingleInt(final int rawValue, final IntFunction<? extends This> constructor, boolean applyRules)
	{
		raw = applyRules ? _rules().apply(rawValue) : rawValue;
		_constructor = requireNonNull(constructor);
	}
	
	// Cache of rules of SingleInt subclasses
	// The map instance itself is never mutated; each update copies and replaces the reference below.
	// The contents come from each subclass's RULES constant, so if an entry is lost due to a race condition,
	// exactly the same value will be regenerated and added to the cache.
	private static Map<Class<? extends SingleInt<?>>, IntRule> _RULES = new HashMap<>();
	
	private IntRule _rules()
	{
		// Find a cached rule for This class
		@SuppressWarnings("unchecked") final Class<This> single = (Class<This>)this.getClass();
		@SuppressWarnings("unchecked") final IntRule rules = _RULES.get(single);
		if(rules != null) return rules;
		
		// Build a new rule from the IntRule constants declared in This class
		final IntRule newRule = IntRule.combine(IntRule.rulesForClass(single));
		
		// Copy and replace the cache with the added rule
		final Map<Class<? extends SingleInt<?>>, IntRule> rulesCache = new HashMap<>(_RULES);
		rulesCache.put(single, newRule);
		_RULES = rulesCache;
		return newRule;
	}
	
	public final int raw() { return raw; }
	
	@Override public int getAsInt() { return raw; }
	
	@Override public final int hashCode() { return Integer.hashCode(raw); }
	@Override public String toString() { return Integer.toString(raw); }
	
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
		return validIf(raw -> raw > lowerBound, raw -> raw + " <= " + lowerBound);
	}
	
	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	public static IntRule lessThan(final int upperBound)
	{
		return validIf(raw -> raw < upperBound, raw -> raw + " >= " + upperBound);
	}
	
	/** Generate rule to normalise the raw integer value to a minimum floor value. */
	public static IntRule floor(final int minValue) { return raw -> Math.max(raw, minValue); }
	
	/** Generate rule to normalise the raw integer value to a maximum ceiling value. */
	public static IntRule ceiling(final int maxValue) { return raw -> Math.min(raw, maxValue); }
	
	@Override public Integer asNumber() { return raw; }
	
	@Override public final int compareTo(final This that) { return Integer.compare(this.raw, that.raw); }
	
	@Override
	public final int compareToNumber(final Number number)
	{
		return Integer.compare(this.raw, number.intValue());
	}
	
	public final int compareToNumber(final int number)
	{
		return Integer.compare(this.raw, number);
	}
	
	@Override public boolean isZero() { return raw == 0; }
	@Override public boolean isPositive() { return raw > 0; }
	@Override public boolean isNegative() { return raw < 0; }
	
	@Override public final This plus(final Number number) { return plus(number.intValue()); }
	@Override public final This minus(final Number number) { return minus(number.intValue()); }
	@Override public final This multiplyBy(final Number number) { return multiplyBy(number.intValue()); }
	@Override public final This divideBy(final Number number) { return divideBy(number.intValue()); }
	
	public final This plus(final int number) { return map($ -> $ + number); }
	public final This minus(final int number) { return map($ -> $ - number); }
	public final This multiplyBy(final int number) { return map($ -> $ * number); }
	public final This divideBy(final int number) { return map($ -> $ / number); }
	
	public final This plus(final IntSupplier number) { return plus(number.getAsInt()); }
	public final This minus(final IntSupplier number) { return minus(number.getAsInt()); }
	public final This multiplyBy(final IntSupplier number) { return multiplyBy(number.getAsInt()); }
	public final This divideBy(final IntSupplier number) { return divideBy(number.getAsInt()); }
	
	public final boolean isGreaterThan(final IntSupplier number) { return raw > number.getAsInt(); }
	public final boolean isGreaterThanOrEqualTo(final IntSupplier number) { return raw >= number.getAsInt(); }
	public final boolean isLessThan(final IntSupplier number) { return raw < number.getAsInt(); }
	public final boolean isLessThanOrEqualTo(final IntSupplier number) { return raw <= number.getAsInt(); }
	
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
