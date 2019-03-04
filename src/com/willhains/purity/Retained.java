package com.willhains.purity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Indicates that a method argument or return value is retained by the method, or by one or more methods to which it is
 * passed from inside the method.
 */
@Retention(RetentionPolicy.CLASS)
@Target({METHOD, PARAMETER})
public @interface Retained
{
}
