package com.willhains.purity.rule;

import com.willhains.purity.Single;
import com.willhains.purity.annotations.Mutable;
import com.willhains.purity.annotations.Pure;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Normalise and/or validate raw data before it is wrapped in a {@link Single} or other {@link Pure} object.
 *
 * @param <Raw> the raw type to be validated/normalised.
 * @author willhains
 */
public @FunctionalInterface interface Rule<Raw>
{
	/** @return An empty rule that does nothing. */
	@SuppressWarnings("unchecked") static <Raw> Rule<Raw> none() { return (Rule<Raw>)NONE; }
	static final Rule<?> NONE = raw -> raw;
	
	/** Applies this rule to the given argument. */
	Raw applyTo(Raw raw) throws IllegalArgumentException;
	
	/**
	 * @return the combination of constants of type {@link Rule} declared in the given {@link Single} subclass.
	 */
	static <Raw, This extends Single<Raw, This>> Rule<Raw> rulesForClass(final Class<This> single)
	{
		return RulesCache.current.getRulesFor(single);
	}
	
	/** Combine multiple rules into a single rule. */
	@SafeVarargs
	static <Raw> Rule<Raw> combine(final Rule<Raw>... combiningRules)
	{
		return raw ->
		{
			Raw result = raw;
			for(final Rule<Raw> rule: combiningRules) result = rule.applyTo(result);
			return result;
		};
	}
	
	/**
	 * Convert the {@link Predicate} `condition` into a {@link Rule} where `condition` must evaluate to `true`.
	 *
	 * @param condition the raw value must satisfy this condition to be valid.
	 * @param errorMessageFactory generate the text of {@link IllegalArgumentException} when the condition is not met.
	 * @param <Raw> the raw value type to be validated.
	 * @return a {@link Rule} that passes the value through as-is, unless `condition` is not satisfied.
	 */
	static <Raw> Rule<Raw> validIf(
		final Predicate<? super Raw> condition,
		final Function<? super Raw, String> errorMessageFactory)
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
	 * @param <Raw> the raw value type to be validated.
	 * @return a {@link Rule} that passes the value through as-is, unless `condition` is satisfied.
	 */
	static <Raw> Rule<Raw> validUnless(
		final Predicate<? super Raw> condition,
		final Function<? super Raw, String> errorMessageFactory)
	{
		return validIf(condition.negate(), errorMessageFactory);
	}
	
	/**
	 * @see #validIf(Predicate,Function)
	 */
	static <Raw> Rule<Raw> validIf(final Predicate<? super Raw> condition, final String errorMessage)
	{
		return validIf(condition, $ -> errorMessage);
	}
	
	/**
	 * @see #validUnless(Predicate,Function)
	 */
	static <Raw> Rule<Raw> validUnless(final Predicate<? super Raw> condition, final String errorMessage)
	{
		return validIf(condition.negate(), errorMessage);
	}
}

/** Cache of rules of Single subclasses. */
final @Mutable class RulesCache
{
	// The RulesCache instance itself is never mutated; each update copies and replaces the reference below.
	// The contents come from each subclass's Rule constants, so if an entry is lost due to a race condition,
	// exactly the same value will be regenerated and added to the cache.
	static RulesCache current = new RulesCache(new HashMap<>());
	private final Map<Class<? extends Single<?, ?>>, Rule<?>> _rules;
	private RulesCache(final Map<Class<? extends Single<?, ?>>, Rule<?>> rules) { _rules = rules; }
	
	public <Raw, This extends Single<Raw, This>> Rule<Raw> getRulesFor(final Class<This> type)
	{
		// Find a cached rule for the class
		@SuppressWarnings("unchecked") final Rule<Raw> cachedRule = (Rule<Raw>)_rules.get(type);
		if(cachedRule != null) return cachedRule;
		
		// Build a new rule from the Rule constants declared in the class
		@SuppressWarnings("unchecked")
		final Rule<Raw> newRule = Rule.combine(Constants.ofClass(type).getConstantsOfType(Rule.class));
		
		// Copy and replace the cache with the added rule
		final Map<Class<? extends Single<?, ?>>, Rule<?>> updatedCache = new HashMap<>(_rules);
		updatedCache.put(type, newRule);
		current = new RulesCache(updatedCache);
		
		return newRule;
	}
}
