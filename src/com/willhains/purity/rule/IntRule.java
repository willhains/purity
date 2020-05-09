package com.willhains.purity.rule;

import com.willhains.purity.SingleInt;
import com.willhains.purity.annotations.Mutable;
import com.willhains.purity.annotations.Pure;

import java.util.HashMap;
import java.util.Map;
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
	int applyTo(int i);
	
	/**
	 * @return the value of the first constant of type {@link IntRule} declared in the given {@link SingleInt} subclass.
	 */
	static <This extends SingleInt<This>> IntRule rulesForClass(final Class<This> single)
	{
		return IntRulesCache.current.getRulesFor(single);
	}
	
	/** Combine multiple rules into a single rule. */
	static IntRule combine(final IntRule... combiningRules)
	{
		return raw ->
		{
			int result = raw;
			for(final IntRule rule: combiningRules) result = rule.applyTo(result);
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

/** Cache of rules of Single subclasses. */
final @Mutable
class IntRulesCache
{
	// The RulesCache instance itself is never mutated; each update copies and replaces the reference below.
	// The contents come from each subclass's Rule constants, so if an entry is lost due to a race condition,
	// exactly the same value will be regenerated and added to the cache.
	static IntRulesCache current = new IntRulesCache(new HashMap<>());
	private final Map<Class<? extends SingleInt<?>>, IntRule> _rules;
	private IntRulesCache(final Map<Class<? extends SingleInt<?>>, IntRule> rules) { _rules = rules; }
	
	public <This extends SingleInt<This>> IntRule getRulesFor(final Class<This> type)
	{
		// Find a cached rule for the class
		final IntRule cachedRule = _rules.get(type);
		if(cachedRule != null) return cachedRule;
		
		// Build a new rule from the Rule constants declared in the class
		@SuppressWarnings("unchecked")
		final IntRule newRule = IntRule.combine(Constants.ofClass(type).getConstantsOfType(IntRule.class));
		
		// Copy and replace the cache with the added rule
		final Map<Class<? extends SingleInt<?>>, IntRule> updatedCache = new HashMap<>(_rules);
		updatedCache.put(type, newRule);
		current = new IntRulesCache(updatedCache);
		
		return newRule;
	}
}
