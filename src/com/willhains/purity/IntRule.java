package com.willhains.purity;

import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleInt} or other {@link Pure} object.
 *
 * @author willhains
 */
public @FunctionalInterface interface IntRule
{
	/** An empty rule that does nothing. */
	static final IntRule NONE = raw -> raw;
	
	/** Applies this rule to the given argument. */
	int apply(int i);
	
	/**
	 * @return the value of the first constant of type {@link IntRule} declared in the given {@link SingleInt} subclass.
	 */
	static <This extends SingleInt<This>> IntRule[] rulesForClass(final Class<This> single)
	{
		return Constants.ofClass(single).getConstantsOfType(IntRule.class);
	}
	
	/** Combine multiple rules into a single rule. */
	static IntRule allOf(final IntRule... combiningRules)
	{
		return raw ->
		{
			int result = raw;
			for(final IntRule rule: combiningRules) result = rule.apply(result);
			return result;
		};
	}
	
	/**
	 * Convert the {@link Predicate} `condition` into a {@link Rule} where `condition` must evaluate to `true`.
	 *
	 * @param condition the raw value must satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is not met.
	 * @return a {@link Rule} that passes the value through as-is, unless `condition` is not satisfied.
	 */
	static IntRule validIf(
		final IntPredicate condition,
		final IntFunction<String> errorMessageFactory)
	{
		return raw ->
		{
			if(condition.test(raw)) return raw;
			throw new IllegalArgumentException(errorMessageFactory.apply(raw));
		};
	}
	
	/**
	 * Convert the {@link Predicate} `condition` into a {@link Rule} where `condition` must evaluate to `false`.
	 *
	 * @param condition the raw value must not satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is met.
	 * @return a {@link Rule} that passes the value through as-is, unless `condition` is satisfied.
	 */
	static IntRule validUnless(
		final IntPredicate condition,
		final IntFunction<String> errorMessageFactory)
	{
		return validIf(condition.negate(), errorMessageFactory);
	}
	
	/**
	 * @see #validIf(IntPredicate,IntFunction)
	 */
	static IntRule validIf(final IntPredicate condition, final String errorMessage)
	{
		return validIf(condition, $ -> errorMessage);
	}
	
	/**
	 * @see #validUnless(IntPredicate,IntFunction)
	 */
	static IntRule validUnless(final IntPredicate condition, final String errorMessage)
	{
		return validIf(condition.negate(), errorMessage);
	}
}
