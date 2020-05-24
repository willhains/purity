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

	Trim[] trim() default {};
	enum Case { LOWERCASE, UPPERCASE, TITLE_CASE }
	Case[] transformTo() default {};
	int[] truncateTo() default {};
	enum InternPolicy { RAW }
	InternPolicy[] intern() default {};
}

d