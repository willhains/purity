package com.willhains.purity;

import java.util.*;
import java.util.function.Function;

/**
 * Lock-free, thread-safe (but forgetful), copy-on-write index of rules for each Single* class
 *
 * @param <RuleType> the rule interface type to cache.
 * @author willhains
 */
final @Mutable class RulesCache<RuleType>
{
	private Map<Class<?>, RuleType> _rules = new HashMap<>();

	/** May call `createRule` multiple times in race conditions, so its result must be the same every time. */
	public RuleType computeIfAbsent(
		final Class<?> singleClass,
		final Function<Class<?>, ? extends RuleType> createRule)
	{
		// Existing rule
		RuleType rule = _rules.get(singleClass);
		if(rule != null) return rule;

		// Create and register rule
		final Map<Class<?>, RuleType> copy = new HashMap<>(_rules);
		rule = createRule.apply(singleClass);
		copy.put(singleClass, rule);
		_rules = copy;
		return rule;
	}
}
