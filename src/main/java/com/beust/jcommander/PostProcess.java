package com.beust.jcommander;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Copyright (C) 2015 the original author or authors.
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

/**
 * An method annotation to ask JCommander to run the specified method after all parameters have been processed as an additional validation step. The annotated
 * method MUST NOT take any arguments. Any exceptions thrown by the annotated method will be wrapped in {@link ParameterException} and re-thrown. If the
 * annotated method returns a boolean, then <tt>true</tt> will be taken to mean that validation passed and <tt>false</tt> will mean that validation failed and a
 * ParameterException with no message or cause will be thrown. If the method returns anything else (other than null), then the String form of that object will
 * be taken as the error message and a ParameterException thrown.
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({METHOD})
public @interface PostProcess {

}
