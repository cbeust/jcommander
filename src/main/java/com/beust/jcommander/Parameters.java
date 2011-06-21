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

  /**
   * If the annotated class was added to {@link JCommander} as a command with
   * {@link JCommander#addCommand}, then this string will be displayed in the
   * description when @{link JCommander#usage} is invoked.
   */
  String commandDescription() default "";

  /**
   * Command name to be used when an annotated class is added using
   * {@link JCommander#addCommand}. If command name is not specified
   * in the annotation, it has to be passed during command addition.
   * Command name given to {@link JCommander#addCommand} takes precedence
   * over the the name specified in the annotation.
   * @return
   */
  String commandName() default "";

  /**
   * Command aliases to be used when an annotated class is added using
   * {@link JCommander#addCommand(Object)} or {@link JCommander#addCommand(String, Object)}.
   * If {@link JCommander#addCommand(String, Object, String...)} method is used, then
   * aliases passed to the method take precedence over any aliases specified
   * in the annotation.
   * @return
   */
  String[] commandAliases() default {};
}
