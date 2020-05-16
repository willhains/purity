package com.willhains.purity.annotations;

import java.lang.annotation.*;
import java.math.RoundingMode;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented @Retention(RUNTIME) @Target(TYPE)
public @interface Adjust
{
	double[] floor() default {};
	double[] ceiling() default {};
	double[] roundToIncrement() default {};
	RoundingMode rounding() default RoundingMode.HALF_UP;

	boolean trimWhitespace() default false;
	boolean lowercase() default false;
	boolean uppercase() default false;
	int[] truncate() default {};
	boolean internRaw() default false;
}

