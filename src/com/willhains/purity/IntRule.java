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
	/** Applies this rule to the given argument. */
	int applyRule(int i);
	
	static <This extends SingleInt<This>> IntRule rulesForClass(final Class<This> single)
	{
		/* Nullable */ IntRule rules = Rule.getConstant(single, "RULES");
		if(rules == null) rules = Rule.getConstant(single, "_RULES");
		if(rules == null) rules = rules(); // empty
		return rules;
	}
	
	/** Combine multiple rules into a single rule. */
	static IntRule rules(final IntRule... combiningRules)
	{
		return raw ->
		{
			int result = raw;
			for(final IntRule rule: combiningRules) result = rule.applyRule(result);
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
	static IntRule validOnlyIf(
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
		return validOnlyIf(condition.negate(), errorMessageFactory);
	}
	
	/**
	 * @see #validOnlyIf(IntPredicate,IntFunction)
	 */
	static IntRule validOnlyIf(final IntPredicate condition, final String errorMessage)
	{
		return validOnlyIf(condition, $ -> errorMessage);
	}
	
	/**
	 * @see #validUnless(IntPredicate,IntFunction)
	 */
	static IntRule validUnless(final IntPredicate condition, final String errorMessage)
	{
		return validOnlyIf(condition.negate(), errorMessage);
	}
}
