package com.willhains.purity.annotations;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * Indicates that a method argument or return value may be retained by the method, or by one or more methods it calls.
 *
 * Formally, the annotated argument / return value is not {@link Released} or {@link Returned}.
 *
 * Method arguments and return values of non-{@link Pure} classes are treated as {@link Retained} by default, unless
 * explicitly annotated otherwise.
 *
 * @see Released
 * @see Returned
 * @author willhains
 */
@Retention(RetentionPolicy.CLASS)
@Target({METHOD, PARAMETER})
public @interface Retained {}
