package com.beust.jcommander;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation used to specify settings for parameter parsing.
 * 
 * @author cbeust
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface Parameters {

  public static final String DEFAULT_OPTION_PREFIXES = "-";

  /**
   * The name of the resource bundle to use for this class.
   */
  String resourceBundle() default "";

  /**
   * The character(s) that separate options.
   */
  String separators() default " ";

  /**
   * What characters an option starts with.
   */
  String optionPrefixes() default DEFAULT_OPTION_PREFIXES;
}
