package com.willhains.purity;

import java.util.*;
import java.util.function.*;

/**
 * Lock-free, thread-safe (but forgetful), copy-on-write index of rules for each Single* class.
 *
 * @param <RuleType> the *Rule interface to cache.
 * @author willhains
 * @deprecated Purity has moved to <a href="https://github.com/willhains/udtopia">UDTopia</a>.
 *   Use {@link org.udtopia.rules.RulesCache} instead.
 */
@Deprecated
final @Mutable class CopyOnWriteRulesCache<RuleType> implements RulesCache<RuleType>
{
	private Map<Class<?>, RuleType> _rules = new HashMap<>();

	@Override public RuleType computeIfAbsent(
		final Class<?> singleClass,
		final Function<? super Class<?>, ? extends RuleType> createRule)
	{
		// Existing rule
		final RuleType existingRule = _rules.get(singleClass);
		if(existingRule != null) return existingRule;

		// Create and register rule
		final Map<Class<?>, RuleType> copy = new HashMap<>(_rules);
		final RuleType newRule = createRule.apply(singleClass);
		copy.put(singleClass, newRule);
		_rules = copy;
		return newRule;
	}
}
