package com.willhains.purity;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Validate the raw values of {@link SingleString}, {@link SingleInt}, {@link SingleLong}, {@link SingleDecimal}, or
 * {@link SingleDouble} subclasses, to ensure their values are always valid. Doing so tends to push data validation
 * out to the edges of an application, where it receives input from the outside world, freeing the internal logic from
 * the need to think about the possibility of invalid values.
 *
 * @author willhains
 * @deprecated Purity has moved to <a href="https://github.com/willhains/udtopia">UDTopia</a>.
 *   Use validator annotations in {@link org.udtopia.rules} instead.
 */
@Deprecated
@Documented @Retention(RUNTIME) @Target(TYPE)
public @interface Validate
{
	/**
	 * What to do when a raw value violates the validation rules.
	 * Defaults to {@link ValidationPolicy#THROW}, which throws an {@link IllegalArgumentException}.
	 */
	ValidationPolicy onFailure() default ValidationPolicy.THROW;

	/** Lower bound (inclusive) for numeric values, or the length of strings. */
	double[] min() default {};

	/** Upper bound (inclusive) for numeric values, or the length of strings. */
	double[] max() default {};

	/** Lower bound (exclusive) for numeric values, or the length of strings. */
	double[] greaterThan() default {};

	/** Numeric upper bound (exclusive) for numeric values, or the length of strings. */
	double[] lessThan() default {};

	/** Number must be divisible by this. Note that floating point non-integer values only work if they end in '5'. */
	double[] multipleOf() default {};

	/** Set true to allow infinite floating-point values. */
	boolean allowInfinity() default false;

	/** Set true to allow not-a-number floating-point values. */
	boolean allowNaN() default false;

	/** Strings containing all allowed characters. */
	String[] chars() default {};

	/** Strings containing all disallowed characters. */
	String[] notChars() default {};

	/** Allowed regex patterns for string values. */
	String[] match() default {};

	/** Disallowed regex patterns for string values. */
	String[] notMatch() default {};

	/** Letters (of the English alphabet). */
	String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/** Digit characters. */
	String DIGITS = "0123456789";
}
