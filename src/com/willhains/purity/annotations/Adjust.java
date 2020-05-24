package com.willhains.purity.annotations;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented @Retention(RUNTIME) @Target(TYPE)
public @interface Adjust
{
	double[] floor() default {};
	double[] ceiling() default {};

	Trim[] trim() default {};
	LetterCase[] transformTo() default {};
}
