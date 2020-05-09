package com.willhains.purity.rule;

import com.willhains.purity.SingleLong;
import com.willhains.purity.annotations.Mutable;
import com.willhains.purity.annotations.Pure;

import java.util.HashMap;
import java.util.Map;
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
	long apply(long i);
	
	/**
	 * @return the value of the first constant of type {@link LongRule} declared in the given {@link SingleLong} subclass.
	 */
	static <This extends SingleLong<This>> LongRule rulesForClass(final Class<This> single)
	{
		return LongRulesCache.current.getRulesFor(single);
	}
	
	/** Combine multiple rules into a single rule. */
	static LongRule combine(final LongRule... combiningRules)
	{
		return raw ->
		{
			long result = raw;
			for(final LongRule rule: combiningRules) result = rule.apply(result);
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
	static LongRule validIf(
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
		return validIf(condition.negate(), errorMessageFactory);
	}
	
	/**
	 * @see #validIf(LongPredicate,LongFunction)
	 */
	static LongRule validIf(final LongPredicate condition, final String errorMessage)
	{
		return validIf(condition, $ -> errorMessage);
	}
	
	/**
	 * @see #validUnless(LongPredicate,LongFunction)
	 */
	static LongRule validUnless(final LongPredicate condition, final String errorMessage)
	{
		return validIf(condition.negate(), errorMessage);
	}
}

/** Cache of rules of Single subclasses. */
final @Mutable class LongRulesCache
{
	// The RulesCache instance itself is never mutated; each update copies and replaces the reference below.
	// The contents come from each subclass's Rule constants, so if an entry is lost due to a race condition,
	// exactly the same value will be regenerated and added to the cache.
	static LongRulesCache current = new LongRulesCache(new HashMap<>());
	private final Map<Class<? extends SingleLong<?>>, LongRule> _rules;
	private LongRulesCache(final Map<Class<? extends SingleLong<?>>, LongRule> rules) { _rules = rules; }
	
	public <This extends SingleLong<This>> LongRule getRulesFor(final Class<This> type)
	{
		// Find a cached rule for the class
		final LongRule cachedRule = _rules.get(type);
		if(cachedRule != null) return cachedRule;
		
		// Build a new rule from the Rule constants declared in the class
		@SuppressWarnings("unchecked")
		final LongRule newRule = LongRule.combine(Constants.ofClass(type).getConstantsOfType(LongRule.class));
		
		// Copy and replace the cache with the added rule
		final Map<Class<? extends SingleLong<?>>, LongRule> updatedCache = new HashMap<>(_rules);
		updatedCache.put(type, newRule);
		current = new LongRulesCache(updatedCache);
		
		return newRule;
	}
}
