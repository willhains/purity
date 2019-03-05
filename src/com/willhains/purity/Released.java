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
 * Formally, the annotated argument / return value:
 * <ol>
 *     <li>Is not stored in a field of the method's class.</li>
 *     <li>Is not stored directly in a field of another class.</li>
 *     <li>Is not passed to a constructor or method as a {@link Retained} argument.</li>
 *     <li>If passed to a constructor or method as a {@link Released} argument, the return value is released.</li>
 * </ol>
 *
 * Method arguments and return values of {@link Pure} classes are {@link Released} by default, since pure values are
 * immutable and therefore unable to retain them.
 *
 * @see Retained
 * @see Released
 */
@Retention(RetentionPolicy.CLASS)
@Target({METHOD, PARAMETER})
public @interface Released
{
}
