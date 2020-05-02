package com.willhains.purity;

import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleLong} or other {@link Pure} object.
 *
 * @author willhains
 */
public @FunctionalInterface interface LongRule
{
	/** An empty rule that does nothing. */
	static final LongRule NONE = raw -> raw;
	
	/** Applies this rule to the given argument. */
	long applyRule(long i);
	
	static <This extends SingleLong<This>> LongRule rulesForClass(final Class<This> single)
	{
		/* Nullable */ LongRule rules = Rule.getConstant(single, "RULES");
		if(rules == null) rules = Rule.getConstant(single, "_RULES");
		if(rules == null) rules = all(); // empty
		return rules;
	}
	
	/** Combine multiple rules into a single rule. */
	static LongRule all(final LongRule... combiningRules)
	{
		return raw ->
		{
			long result = raw;
			for(final LongRule rule: combiningRules) result = rule.applyRule(result);
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
	static LongRule validOnlyIf(
		final LongPredicate condition,
		final LongFunction<String> errorMessageFactory)
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
	static LongRule validUnless(
		final LongPredicate condition,
		final LongFunction<String> errorMessageFactory)
	{
		return validOnlyIf(condition.negate(), errorMessageFactory);
	}
	
	/**
	 * @see #validOnlyIf(LongPredicate,LongFunction)
	 */
	static LongRule validOnlyIf(final LongPredicate condition, final String errorMessage)
	{
		return validOnlyIf(condition, $ -> errorMessage);
	}
	
	/**
	 * @see #validUnless(LongPredicate,LongFunction)
	 */
	static LongRule validUnless(final LongPredicate condition, final String errorMessage)
	{
		return validOnlyIf(condition.negate(), errorMessage);
	}
}
