package com.willhains.purity;

import java.lang.annotation.*;
import java.math.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Normalise the raw values of {@link SingleString}, {@link SingleInt}, {@link SingleLong}, {@link SingleDecimal}, or
 * {@link SingleDouble} subclasses, to ensure their values are always valid. Doing so tends to push data normalisation
 * out to the edges of an application, where it receives input from the outside world, freeing the internal logic from
 * the need to think about the possibility of invalid values.
 *
 * @author willhains
 */
@Documented @Retention(RUNTIME) @Target(TYPE)
public @interface Adjust
{
	/** Adjust numeric values to a minimum (inclusive) value. */
	double[] floor() default {};

	/** Adjust numeric values to a maximum (inclusive) value. */
	double[] ceiling() default {};

	/** Trim from the beginning and/or end of string values. */
	Trim[] trim() default {};

	/** Transform string values. */
	LetterCase[] transformTo() default {};

	/** {@linkplain String#intern Intern} string values. */
	boolean intern() default false;

	/** Round numeric values to an increment (multiple). Rounding method is {@link RoundingMode#HALF_UP} */
	double[] roundToIncrement() default {};
}
