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
import static java.lang.annotation.ElementType.METHOD;

import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.converters.IParameterSplitter;
import com.beust.jcommander.converters.NoConverter;
import com.beust.jcommander.validators.NoValidator;
import com.beust.jcommander.validators.NoValueValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD })
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
   * The string converter to use for this field. If the field is of type <tt>List</tt>
   * and not <tt>listConverter</tt> attribute was specified, JCommander will split
   * the input in individual values and convert each of them separately.
   */
  Class<? extends IStringConverter<?>> converter() default NoConverter.class;

  /**
   * The list string converter to use for this field. If it's specified, the
   * field has to be of type <tt>List</tt> and the converter needs to return
   * a List that's compatible with that type.
   */
  Class<? extends IStringConverter<?>> listConverter() default NoConverter.class;

  /**
   * If true, this parameter won't appear in the usage().
   */
  boolean hidden() default false;

  /**
   * Validate the parameter found on the command line.
   */
  Class<? extends IParameterValidator> validateWith() default NoValidator.class;

  /**
   * Validate the value for this parameter.
   */
  Class<? extends IValueValidator> validateValueWith() default NoValueValidator.class;

  /**
   * @return true if this parameter has a variable arity. See @{IVariableArity}
   */
  boolean variableArity() default false;

  /**
   * What splitter to use (applicable only on fields of type <tt>List</tt>). By default,
   * a comma separated splitter will be used.
   */
  Class<? extends IParameterSplitter> splitter() default CommaParameterSplitter.class;
  
  /**
   * If true, console will not echo typed input
   * Used in conjunction with password = true
   */
  boolean echoInput() default false;

  /**
   * If true, this parameter is for help. If such a parameter is specified,
   * required parameters are no longer checked for their presence.
   */
  boolean help() default false;
}
