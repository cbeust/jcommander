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

package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Test enums lists.
 *
 * @author Julien Nicoulaud
 */
public class ArgsEnumList {

  @Parameter(names = "-choices")
  public List<ArgsEnum.ChoiceType> choices = new ArrayList<ArgsEnum.ChoiceType>(EnumSet.allOf(ArgsEnum.ChoiceType.class));

}
