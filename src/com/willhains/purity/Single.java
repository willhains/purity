package com.willhains.purity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * A value type wrapping an underlying data type.
 * This supertype and its abstract subtypes make it easy to implement {@link Pure} value types.
 *
 * @param <Raw> The underlying type.
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
 */
public abstract @Pure class Single<Raw, This extends Single<Raw, This>>
{
	// The single-argument constructor of the subclass
	private final Function<? super Raw, ? extends This> _constructor;
	
	// Cache of rules of Single subclasses
	// The map instance itself is never mutated; each update copies and replaces the reference below.
	// The contents come from each subclass's RULES constant, so if an entry is lost due to a race condition,
	// exactly the same value will be regenerated and added to the cache.
	private static Map<Class<? extends Single<?, ?>>, Rule<?>> _RULES = new HashMap<>();
	
	/** The raw underlying value. Do not mutate! */
	protected final Raw raw;
	
	/**
	 * @param rawValue The raw value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected Single(final Raw rawValue, final Function<? super Raw, ? extends This> constructor)
	{
		this.raw = _rules().applyRule(requireNonNull(rawValue));
		_constructor = requireNonNull(constructor);
	}
	
	private Rule<Raw> _rules()
	{
		final Class<This> single = (Class<This>)this.getClass();
		/* Nullable */ Rule<Raw> rules = (Rule<Raw>)_RULES.get(single);
		if(rules != null) return rules;
		rules = Rule.rulesForClass(single);
		final Map<Class<? extends Single<?, ?>>, Rule<?>> rulesCache = new HashMap<>(_RULES);
		rulesCache.put(single, rules);
		_RULES = rulesCache;
		return rules;
	}
	
	/**
	 * Override this method if {@link Raw} is mutable.
	 *
	 * If the underlying {@link Raw} type is immutable, return the {@link #raw} value as-is (default behaviour).
	 * Otherwise, return a defensive copy.
	 * 
	 * The returned {@link Raw} value, if mutable, could be mutated, so the overriding method should be careful to
	 * make a deep, defensive copy to return to the caller.
	 */
	public Raw raw() { return raw; }
	
	@Override
	public final int hashCode()
	{
		return hashCode(this.raw);
	}
	
	/** Generate a hash code for {@code object}. If {@code object} is an array, combine the hashes of each element. */
	public static int hashCode(Object object)
	{
		if(!object.getClass().isArray()) return object.hashCode();
		
		if(object instanceof Object[]) return Arrays.deepHashCode((Object[]) object);
		if(object instanceof int[]) return Arrays.hashCode((int[]) object);
		if(object instanceof byte[]) return Arrays.hashCode((byte[]) object);
		if(object instanceof boolean[]) return Arrays.hashCode((boolean[]) object);
		if(object instanceof long[]) return Arrays.hashCode((long[]) object);
		if(object instanceof double[]) return Arrays.hashCode((double[]) object);
		if(object instanceof float[]) return Arrays.hashCode((float[]) object);
		if(object instanceof char[]) return Arrays.hashCode((char[]) object);
		return Arrays.hashCode((short[]) object);
	}
	
	@Override
	public final boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final This that = (This) other;
		return equals(this.raw, that.raw);
	}
	
	public final boolean equals(final This that)
	{
		if(that == this) return true;
		if(that == null) return false;
		return equals(this.raw, that.raw);
	}
	
	/** Compare two objects for equality. If they are arrays, compare their elements. */
	public static boolean equals(Object object1, Object object2)
	{
		if(object1 == object2) return true;
		if(object1 == null || object2 == null) return false;
		if(object1.equals(object2)) return true;
		if(!object1.getClass().isArray()) return false;
		
		if(object1 instanceof Object[]) return Arrays.deepEquals((Object[])object1, (Object[])object2);
		if(object1 instanceof int[]) return Arrays.equals((int[])object1, (int[])object2);
		if(object1 instanceof byte[]) return Arrays.equals((byte[])object1, (byte[])object2);
		if(object1 instanceof boolean[]) return Arrays.equals((boolean[])object1, (boolean[])object2);
		if(object1 instanceof long[]) return Arrays.equals((long[])object1, (long[])object2);
		if(object1 instanceof double[]) return Arrays.equals((double[])object1, (double[])object2);
		if(object1 instanceof float[]) return Arrays.equals((float[])object1, (float[])object2);
		if(object1 instanceof char[]) return Arrays.equals((char[])object1, (char[])object2);
		return Arrays.equals((short[])object1, (short[])object2);
	}
	
	@Override
	public String toString()
	{
		return toString(this.raw);
	}
	
	/** Format a string to represent {@code object}. If {@code object} is an array, include each element. */
	public static String toString(Object object)
	{
		if(!object.getClass().isArray()) return String.valueOf(object);
		
		if(object instanceof Object[]) return Arrays.toString((Object[]) object);
		if(object instanceof int[]) return Arrays.toString((int[]) object);
		if(object instanceof byte[]) return Arrays.toString((byte[]) object);
		if(object instanceof boolean[]) return Arrays.toString((boolean[]) object);
		if(object instanceof long[]) return Arrays.toString((long[]) object);
		if(object instanceof double[]) return Arrays.toString((double[]) object);
		if(object instanceof float[]) return Arrays.toString((float[]) object);
		if(object instanceof char[]) return Arrays.toString((char[]) object);
		return Arrays.toString((short[]) object);
	}
	
	/**
	 * Test the raw value with {@code condition}.
	 * This method is useful when using {@link Optional#filter} or {@link Stream#filter}.
	 * <pre>
	 * optional.filter(x -&gt; x.is(String::isEmpty))
	 * </pre>
	 *
	 * @param condition a {@link Predicate} that tests the raw value type.
	 * @return {@code true} if the underlying {@link #raw} value satisfies {@code condition};
	 *         {@code false} otherwise.
	 */
	public final boolean is(final Predicate<? super Raw> condition) { return condition.test(raw()); }
	
	/** Reverse of {@link #is(Predicate)}. */
	public final boolean isNot(final Predicate<? super Raw> condition) { return !is(condition); }
	
	/**
	 * Test the raw value by {@code condition}.
	 *
	 * @return an {@link Optional} containing this instance if the condition is met; empty otherwise.
	 * @see #is(Predicate)
	 */
	public final Optional<This> filter(final Predicate<? super Raw> condition)
	{
		@SuppressWarnings("unchecked") final This self = (This)this;
		return Optional.of(self).filter(it -> it.is(condition));
	}
	
	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return A new instance of this type.
	 */
	public final This map(final Function<? super Raw, ? extends Raw> mapper)
	{
		final Raw mapped = mapper.apply(raw());
		@SuppressWarnings("unchecked") final This self = (This)this;
		if(mapped.equals(raw())) return self;
		return _constructor.apply(mapped);
	}
	
	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return The value returned by {@code mapper}.
	 */
	public final This flatMap(final Function<? super Raw, ? extends This> mapper) { return mapper.apply(raw()); }
}
