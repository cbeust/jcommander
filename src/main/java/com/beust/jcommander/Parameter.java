/**
 * Copyright (C) 2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beust.jcommander;

import static java.lang.annotation.ElementType.FIELD;

import com.beust.jcommander.converters.NoConverter;
import com.beust.jcommander.validators.NoValidator;

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

  /**
   * How many parameter values this parameter will consume. For example,
   * an arity of 2 will allow "-pair value1 value2".
   */
  int arity() default -1;

  /**
   * If true, this parameter is a password and it will be prompted on the console
   * (if available).
   */
  boolean password() default false;

  /**
   * The string converter to use for this field.
   */
  Class<? extends IStringConverter<?>> converter() default NoConverter.class;

  /**
   * If true, this parameter won't appear in the usage().
   */
  boolean hidden() default false;

  /**
   * The validation class to use.
   */
  Class<? extends IParameterValidator> validateWith() default NoValidator.class;
}
