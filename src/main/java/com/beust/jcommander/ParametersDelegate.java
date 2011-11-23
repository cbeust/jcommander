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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * <p>When applied to a field all of its child fields annotated
 * with {@link Parameter} will be included during arguments
 * parsing.</p>
 *
 * <p>Mainly useful when creating complex command based CLI interfaces,
 * where several commands can share a set of arguments, but using
 * object inheritance is not enough, due to no-multiple-inheritance
 * restriction. Using {@link ParametersDelegate} any number of
 * command sets can be shared by using composition pattern.</p>
 *
 * <p>Delegations can be chained (nested).</p>
 * 
 * @author rodionmoiseev
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ FIELD })
public @interface ParametersDelegate {
}
