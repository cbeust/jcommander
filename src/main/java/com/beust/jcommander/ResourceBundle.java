package com.beust.jcommander;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @deprecated, use @Parameters
 * 
 * @author cbeust
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface ResourceBundle {
  /**
   * The name of the resource bundle to use for this class.
   */
  String value();
}
