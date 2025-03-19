package com.beust.jcommander;

import static java.lang.annotation.ElementType.FIELD;

import com.beust.jcommander.validators.NoValidator;
import com.beust.jcommander.validators.NoValueValidator;

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

  /**
   * If true, this parameter won't appear in the usage().
   */
  boolean hidden() default false;

  /**
   * The validation classes to use.
   */
  Class<? extends IParameterValidator>[] validateWith() default NoValidator.class;

  /**
   * The character(s) used to assign the values.
   */
  String assignment() default "=";

  Class<? extends IValueValidator>[] validateValueWith() default NoValueValidator.class;

  /**
   * If specified, this number will be used to order the description of this parameter when usage() is invoked.
   * @return
   */
  int order() default -1;

  /**
   * If specified, the category name will be used to order the description of this parameter when usage() is invoked before the number order() is used.
   * @return (default or specified) category name
   */
  String category() default "";
  
  /**
   * If specified, the placeholder (e.g. {@code "<filename>"}) will be shown in the usage() output as a required parameter after the switch (e.g. {@code -i <filename>}).
   * @return the placeholder
   */
  String placeholder() default "";
}
