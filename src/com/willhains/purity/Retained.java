package com.willhains.purity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Indicates that a method argument or return value is retained by the method, or by one or more methods to which it is
 * passed from inside the method.
 *
 * Formally, the annotated argument / return value is not {@link Released} or {@link Returned}.
 *
 * Method arguments and return values of non-{@link Pure} classes are treated as {@link Retained} by default, unless
 * explicitly annotated otherwise.
 *
 * @see Released
 * @see Retained
 */
@Retention(RetentionPolicy.CLASS)
@Target({METHOD, PARAMETER})
public @interface Retained
{
}
