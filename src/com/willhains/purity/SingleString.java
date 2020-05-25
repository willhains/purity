package com.willhains.purity;

import com.willhains.purity.annotations.Pure;

import java.util.Optional;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * A value type wrapping a {@link String}.
 *
 * @param <This> Self-reference to the subclass type itself.
 * @author willhains
 */
public abstract @Pure class SingleString<This extends SingleString<This>>
	implements SingleComparable<This>, CharSequence, Supplier<String>
{
	// The single-argument constructor of the subclass
	private final Function<? super String, ? extends This> _constructor;

	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	private final String _raw;

	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleString(final String rawValue, final Function<? super String, ? extends This> constructor)
	{
		_raw = StringRule.rulesForClass(this.getClass()).applyTo(rawValue);
		_constructor = requireNonNull(constructor);
	}
	
	public final String raw() { return _raw; }
	
	@Override public String get() { return _raw; }
	
	@Override public String toString() { return _raw; }
	@Override public int compareTo(This that) { return this.raw().compareTo(that.raw()); }
	
	@Override public final int length() { return _raw.length(); }
	@Override public final char charAt(final int position) { return _raw.charAt(position); }
	public final char charAt(final IntSupplier position) { return charAt(position.getAsInt()); }
	
	/** @return a new value of the same type from a substring. */
	@Override
	public final This subSequence(final int start, final int end) { return map(s -> s.substring(start, end)); }
	
	public final This subSequence(final IntSupplier start, final IntSupplier end)
	{
		return subSequence(start.getAsInt(), end.getAsInt());
	}
	
	/** @return a new value of the same type, wrapping only the first (up to) {@code length} characters. */
	public final This left(final int length)
	{
		final int lengthOfString = length();
		final int lengthOfSubstring = Math.min(lengthOfString, length);
		return subSequence(0, lengthOfSubstring);
	}
	
	public final This left(final IntSupplier length) { return left(length.getAsInt()); }
	
	/** @return a new value of the same type, wrapping only the last (up to) {@code length} characters. */
	public final This right(final int length)
	{
		final int lengthOfString = length();
		final int lengthOfSubstring = Math.min(lengthOfString, length);
		return subSequence(lengthOfString - lengthOfSubstring, lengthOfString);
	}
	
	public final This right(final IntSupplier length) { return right(length.getAsInt()); }
	
	/** @return a new value of the same type from the trimmed string. */
	public final This trim() { return map(String::trim); }
	
	/** @return {@code true} if the raw string is zero-length; {@code false} otherwise. */
	public final boolean isEmpty() { return is(String::isEmpty); }
	
	/** @return {@code true} if the raw string is non-zero-length; {@code false} otherwise. */
	public final boolean isNotEmpty() { return isNot(String::isEmpty); }
	
	/** @return a new value of the same type with all instances of the specified pattern replaced. */
	public final This replaceRegex(final String regex, final String replacement)
	{
		return map(s -> s.replaceAll(regex, replacement));
	}
	
	public final This replaceRegex(final Supplier<String> regex, final Supplier<String> replacement)
	{
		return replaceRegex(regex.get(), replacement.get());
	}
	
	/** @return a new value of the same type with all instances of the specified literal string replaced. */
	public final This replaceLiteral(final String literal, final String replacement)
	{
		return map(s -> s.replace(literal, replacement));
	}
	
	public final This replaceLiteral(final Supplier<String> literal, final Supplier<String> replacement)
	{
		return replaceLiteral(literal.get(), replacement.get());
	}

	@Override
	public final int hashCode() { return Single.hashCode(this._raw); }

	@Override
	public final boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final This that = (This) other;
		return Single.equals(this.raw(), that.raw());
	}

	public final boolean equals(final This that)
	{
		if(that == this) return true;
		if(that == null) return false;
		return Single.equals(this.raw(), that.raw());
	}

	/**
	 * Test the raw value with {@code condition}.
	 * This method is useful when using {@link Optional#filter} or {@link Stream#filter}.
	 * <pre>
	 * optional.filter(x -&gt; x.is(String::isEmpty))
	 * </pre>
	 *
	 * @param condition a {@link Predicate} that tests the raw value type.
	 * @return {@code true} if the underlying {@link #_raw} value satisfies {@code condition};
	 *         {@code false} otherwise.
	 */
	public final boolean is(final Predicate<? super String> condition) { return condition.test(raw()); }

	/** Reverse of {@link #is(Predicate)}. */
	public final boolean isNot(final Predicate<? super String> condition) { return !is(condition); }

	/**
	 * Test the raw value by {@code condition}.
	 *
	 * @return an {@link Optional} containing this instance if the condition is met; empty otherwise.
	 * @see #is(Predicate)
	 */
	public final Optional<This> filter(final Predicate<? super String> condition)
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
	public final This map(final Function<? super String, ? extends String> mapper)
	{
		final String mapped = mapper.apply(raw());
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
	public final This flatMap(final Function<? super String, ? extends This> mapper) { return mapper.apply(raw()); }
}
