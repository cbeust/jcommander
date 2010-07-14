package com.beust.jcommander;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD })
public @interface Parameter {

  /**
   * An array of allowed command line parameters (e.g. "-d", "--outputdir", etc...).
   * If this attribute is omitted, the field it's annotating will receive all the
   * unparsed options. There can only be at most one such annotation.
   */
  String[] names() default {};

  /**
   * A description of this option.
   */
  String description() default "";

  /**
   * Whether this option is required.
   */
  boolean required() default false;

  /**
   * The key used to find the string in the message bundle.
   */
  String descriptionKey() default "";
}
