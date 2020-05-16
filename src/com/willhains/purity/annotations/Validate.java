package com.willhains.purity.annotations;

import com.willhains.purity.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validate the raw values of {@link SingleString}, {@link SingleInt}, {@link SingleLong}, {@link SingleDecimal}, or
 * {@link SingleDouble} subclasses, to ensure their values are always valid. Doing so tends to push data validation
 * out to the edges of an application, where it receives input from the outside world, freeing the internal logic from
 * the need to think about the possibility of invalid values.
 */
@Documented @Retention(RUNTIME) @Target(TYPE)
public @interface Validate
{
	/**
	 * What to do when a raw value violates the validation rules. Defaults to {@link OnFailure#THROW}, which throws an
	 * {@link IllegalArgumentException}.
	 */
	OnFailure onFailure() default OnFailure.THROW;

	/** Choices for what to do when validation fails. */
	enum OnFailure
	{
		/** Throw an {@link java.lang.IllegalArgumentException}. */ THROW,
		/** If JVM assertions are enabled, throw an {@link java.lang.AssertionError}. */ ASSERT,
		/** Exit the JVM, with a non-zero error code. */ EXIT,
		/** Ignore validation failures. */ IGNORE
	}

	/**
	 * Numeric lower bound (inclusive).
	 * Applies to: {@link SingleInt}, {@link SingleLong}, {@link SingleDecimal}, {@link SingleDouble}.
 	 */
	double[] min() default {};

	/**
	 * Numeric upper bound (inclusive).
	 * Applies to: {@link SingleInt}, {@link SingleLong}, {@link SingleDecimal}, {@link SingleDouble}.
	 */
	double[] max() default {};

	/**
	 * Numeric lower bound (exclusive).
	 * Applies to: {@link SingleInt}, {@link SingleLong}, {@link SingleDecimal}, {@link SingleDouble}.
	 */
	double[] greaterThan() default {};

	/**
	 * Numeric upper bound (exclusive).
	 * Applies to: {@link SingleInt}, {@link SingleLong}, {@link SingleDecimal}, {@link SingleDouble}.
	 */
	double[] lessThan() default {};

	/**
	 * Number must be divisible by this.
	 * Applies to: {@link SingleInt}, {@link SingleLong}, {@link SingleDecimal}, {@link SingleDouble}.
	 */
	double[] multipleOf() default {}; double[] divisibleBy() default {}; // Same thing

	/**
	 * The value is allowed to be even. Defaults to true (even numbers allowed).
	 * Applies to: {@link SingleInt}, {@link SingleLong}, {@link SingleDecimal}, {@link SingleDouble}.
	 */
	boolean allowEven() default true;

	/**
	 * The value is allowed to be odd.
	 */
	boolean allowOdd() default true;
	boolean allowNegative() default true;
	boolean allowZero() default true;
	boolean allowInfinity() default false;
	boolean allowNaN() default false;

	String[] validCharacters() default {};
	String[] validPatterns() default {};
}
