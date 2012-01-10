package com.beust.jcommander;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD })
public @interface DynamicParameter {
  /**
   * An array of allowed command line parameters (e.g. "-D", "--define", etc...).
   */
  String[] names() default {};

  /**
   * How many parameter values this parameter will consume. For example,
   * an arity of 0 allow "-Da=b", 1 allows "-D a=b" and 2, "-D a b". Larger
   * arities are not allowed for dynamic parameters.
   */
  int arity() default -1;

  /**
   * Whether this option is required.
   */
  boolean required() default false;

  /**
   * A description of this option.
   */
  String description() default "";

  /**
   * The key used to find the string in the message bundle.
   */
  String descriptionKey() default "";
}
