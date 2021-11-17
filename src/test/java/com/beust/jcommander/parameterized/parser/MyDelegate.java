package com.beust.jcommander.parameterized.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is really just a marker with no value to set since the delegated value is true.
 *
 * @author Tim Gallagher
 */
@Target({ ElementType.FIELD,})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyDelegate {
  public boolean delegated() default true;
  
}
