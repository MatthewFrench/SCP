/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * An annotation signifies the annotated cannot be empty.
 */
@Documented
@Target(value={FIELD, PARAMETER, METHOD})
public @interface NonEmpty {
}
