package com.beust.jcommander;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * @author Cedric Beust &lt;cedric@beust.com&gt;
 * @since 02 12, 2017
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD })
public @interface SubParameter {
    int order() default -1;
}
