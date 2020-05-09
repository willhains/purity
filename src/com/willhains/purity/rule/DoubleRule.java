package com.willhains.purity.rule;

import com.willhains.purity.SingleDouble;
import com.willhains.purity.annotations.Mutable;
import com.willhains.purity.annotations.Pure;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link SingleDouble} or other {@link Pure} object.
 *
 * @author willhains
 */
public @FunctionalInterface interface DoubleRule
{
	/** An empty rule that does nothing. */
	static final DoubleRule NONE = raw -> raw;
	
	/** Applies this rule to the given argument. */
	double apply(double i);
	
	/**
	 * @return the value of the first constant of type {@link DoubleRule} declared in the given {@link SingleDouble}
	 *  subclass.
	 */
	static <This extends SingleDouble<This>> DoubleRule rulesForClass(final Class<This> single)
	{
		return DoubleRulesCache.current.getRulesFor(single);
	}
	
	/** Combine multiple rules into a single rule. */
	static DoubleRule combine(final DoubleRule... combiningRules)
	{
		return raw ->
		{
			double result = raw;
			for(final DoubleRule rule: combiningRules) result = rule.apply(result);
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
	static DoubleRule validIf(
		final DoublePredicate condition,
		final DoubleFunction<String> errorMessageFactory)
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
	static DoubleRule validUnless(
		final DoublePredicate condition,
		final DoubleFunction<String> errorMessageFactory)
	{
		return validIf(condition.negate(), errorMessageFactory);
	}
	
	/**
	 * @see #validIf(DoublePredicate,DoubleFunction)
	 */
	static DoubleRule validIf(final DoublePredicate condition, final String errorMessage)
	{
		return validIf(condition, $ -> errorMessage);
	}
	
	/**
	 * @see #validUnless(DoublePredicate,DoubleFunction)
	 */
	static DoubleRule validUnless(final DoublePredicate condition, final String errorMessage)
	{
		return validIf(condition.negate(), errorMessage);
	}
}

/** Cache of rules of Single subclasses. */
final @Mutable class DoubleRulesCache
{
	// The RulesCache instance itself is never mutated; each update copies and replaces the reference below.
	// The contents come from each subclass's Rule constants, so if an entry is lost due to a race condition,
	// exactly the same value will be regenerated and added to the cache.
	static DoubleRulesCache current = new DoubleRulesCache(new HashMap<>());
	private final Map<Class<? extends SingleDouble<?>>, DoubleRule> _rules;
	private DoubleRulesCache(final Map<Class<? extends SingleDouble<?>>, DoubleRule> rules) { _rules = rules; }
	
	public <This extends SingleDouble<This>> DoubleRule getRulesFor(final Class<This> type)
	{
		// Find a cached rule for the class
		final DoubleRule cachedRule = _rules.get(type);
		if(cachedRule != null) return cachedRule;
		
		// Build a new rule from the Rule constants declared in the class
		@SuppressWarnings("unchecked")
		final DoubleRule newRule = DoubleRule.combine(Constants.ofClass(type).getConstantsOfType(DoubleRule.class));
		
		// Copy and replace the cache with the added rule
		final Map<Class<? extends SingleDouble<?>>, DoubleRule> updatedCache = new HashMap<>(_rules);
		updatedCache.put(type, newRule);
		current = new DoubleRulesCache(updatedCache);
		
		return newRule;
	}
}
