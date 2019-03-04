package com.willhains.purity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Indicates that a method argument is not retained (stored in a field) by the method, or by any of the methods to which
 * it is passed from inside the method, except those that are returned to the caller.
 *
 * @see Retained
 * @see Released
 */
@Retention(RetentionPolicy.CLASS)
@Target(PARAMETER)
public @interface Returned
{
}
