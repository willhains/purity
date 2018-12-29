package com.willhains.purity;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Indicates that a type has cache semantics.
 *
 * Formally:
 * <ol>
 * <li>It is final, or all of its subtypes are guaranteed to have cache semantics</li>
 * <li><b>It is mutable</b>, and therefore inherently non-thread-safe</li>
 * <li>It might not have reliable {@link Object#equals equals}, {@link Object#hashCode hashCode},
 *     or {@link Object#toString toString} implementations</li>
 * <li>It has no interaction with input/output whatsoever</li>
 * <li>It has no interaction with concurrency mechanisms whatsoever</li>
 * </ol>
 *
 * @author willhains
 */
@Retention(SOURCE)
@Target({TYPE, TYPE_PARAMETER})
public @interface Mutable {}
