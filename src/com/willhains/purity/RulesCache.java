package com.willhains.purity;

import java.util.function.*;

/**
 * Index of rules for each Single* class.
 *
 * @param <RuleType> the *Rule interface to cache.
 */
@Mutable interface RulesCache<RuleType>
{
	/** May call `createRule` multiple times in race conditions, so its result must be the same every time. */
	RuleType computeIfAbsent(Class<?> singleClass, Function<? super Class<?>, ? extends RuleType> createRule);
}
