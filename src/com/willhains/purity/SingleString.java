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
	implements CharSequence
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
	
	@Override public final int length() { return raw.length(); }
	@Override public final char charAt(final int index) { return raw.charAt(index); }
	
	/** @return a new value of the same type from a substring. */
	@Override
	public final This subSequence(final int start, final int end)
	{
		return map(s -> s.substring(start, end));
	}
}
