package com.willhains.purity;

import java.util.function.Function;
import java.util.regex.Pattern;

import static com.willhains.purity.Rule.validOnlyIf;
import static com.willhains.purity.Rule.validUnless;

/**
 * A value type wrapping a {@link String}.
 *
 * @author willhains
 * @param <This> Self-reference to the subclass type itself.
 */
public abstract @Value class SingleString<This extends SingleString<This>>
	extends Single<String, This>
	implements SingleComparable<This>, CharSequence
{
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleString(final String rawValue, final Function<? super String, ? extends This> constructor)
	{
		super(rawValue, constructor);
	}
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 * @param rules Validation and data normalisation rules for the raw underlying value.
	 */
	protected SingleString(
		final String rawValue,
		final Function<? super String, ? extends This> constructor,
		final Rule<String> rules)
	{
		super(rawValue, constructor, rules);
	}
	
	@Override public String toString() { return raw; }
	@Override public int compareTo(This that) { return this.raw.compareTo(that.raw); }
	
	/** Rule to trim whitespace from beginning and end of raw string value. */
	public static final Rule<String> trimWhitespace = String::trim;
	
	/** Rule to convert the raw string value to lowercase. */
	public static final Rule<String> lowercase = String::toLowerCase;
	
	/** Rule to convert the raw string value to uppercase. */
	public static final Rule<String> uppercase = String::toUpperCase;
	
	/** Letters (of the English alphabet). */
	public static final String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/** Digit characters. */
	public static final String numbers = "0123456789";
	
	/** Generate rule to allow only the characters of `allowedCharacters`. */
	public static Rule<String> validCharacters(final String allowedCharacters)
	{
		final boolean[] validCharMap = new boolean[Character.MAX_VALUE + 1];
		allowedCharacters.chars().forEach(c -> validCharMap[c] = true);
		return validOnlyIf(raw -> raw.chars().allMatch(c -> validCharMap[c]),
			raw -> "\"" + raw + "\" contains invalid characters (valid = " + allowedCharacters + ")");
	}
	
	/** Generate rules to allow only raw strings that match `regExPattern`. */
	public static Rule<String> validPattern(final String regExPattern)
	{
		final Pattern pattern = Pattern.compile(regExPattern);
		return Rule.validOnlyIf(raw -> pattern.matcher(raw).matches(),
			raw -> "\"" + raw + "\" does not match pattern: " + regExPattern);
	}
	
	/** Generate rule to allow only raw strings of length greater than or equal to `length`. */
	public static Rule<String> minLength(final int length)
	{
		return validUnless(raw -> raw.length() < length,
			raw -> "Value \"" + raw + "\" too short: " + raw.length() + " < " + length);
	}
	
	/** Generate rule to allow only raw strings of length less than or equal to `length`. */
	public static Rule<String> maxLength(final int length)
	{
		return validUnless(raw -> raw.length() > length,
			raw -> "Value \\\"\" + raw + \"\\\" too long: " + raw.length() + " > " + length);
	}
	
	/** Intern the raw string. Use this for values that are likely to repeat many times. */
	public static final Rule<String> intern = String::intern;
	
	@Override public final int length() { return raw.length(); }
	@Override public final char charAt(final int position) { return raw.charAt(position); }
	
	/** @return a new value of the same type from a substring. */
	@Override
	public final This subSequence(final int start, final int end)
	{
		return map(s -> s.substring(start, end));
	}
	
	/** @return a new value of the same type, wrapping only the first (up to) {@code length} characters. */
	public final This left(final int length)
	{
		final int lengthOfString = length();
		final int lengthOfSubstring = Math.min(lengthOfString, length);
		return subSequence(0, lengthOfSubstring);
	}
	
	/** @return a new value of the same type, wrapping only the last (up to) {@code length} characters. */
	public final This right(final int length)
	{
		final int lengthOfString = length();
		final int lengthOfSubstring = Math.min(lengthOfString, length);
		return subSequence(lengthOfString - lengthOfSubstring, lengthOfString);
	}
	
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
	
	/** @return a new value of the same type with all instances of the specified literal string replaced. */
	public final This replaceLiteral(final String literal, final String replacement)
	{
		return map(s -> s.replace(literal, replacement));
	}
}
