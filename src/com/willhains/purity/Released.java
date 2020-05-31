package com.willhains.purity;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * Indicates that a method argument or return value is not retained (stored in a field) by the method, or by any objects
 * it calls.
 * <p>
 * Formally, the annotated argument / return value:
 * <ol>
 * <li>Is not stored in a field of the method's class.</li>
 * <li>Is not stored directly in a field of another class.</li>
 * <li>Is not passed to a constructor or method as a {@link Retained} argument.</li>
 * <li>If passed to a constructor or method as a {@link Returned} argument, the return value is released.</li>
 * </ol>
 * <p>
 * Method arguments and return values of {@link Pure} classes are {@link Released} by default, since pure values are
 * immutable and therefore unable to retain them.
 *
 * @author willhains
 * @see Retained
 * @see Returned
 */
@Retention(RetentionPolicy.CLASS)
@Target({METHOD, PARAMETER})
public @interface Released { }
