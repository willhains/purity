package com.willhains.purity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * Indicates that a method argument or return value is not retained (stored in a field) by the method, or by any methods
 * to which it is passed from inside the method.
 *
 * @see Retained
 * @see Released
 */
@Retention(RetentionPolicy.CLASS)
@Target({METHOD, PARAMETER})
public @interface Released
{
}
