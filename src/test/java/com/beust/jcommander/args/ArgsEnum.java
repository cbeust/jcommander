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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.testng.Assert;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * Test enums.
 *
 * @author Adrian Muraru
 */
public class ArgsEnum {

  public enum ChoiceType { ONE, Two, THREE }
  @Parameter(names = "-choice")
  public ChoiceType choice = ChoiceType.ONE;
  
  @Parameter(names = "-choices", variableArity = true)
  public List<ChoiceType> choices = new ArrayList<>();

  public static void main(String[] args1) {
    ArgsEnum args = new ArgsEnum();
    String[] argv = { "-choice", "ONE"};
    JCommander jc = new JCommander(args, argv);
    jc.usage();
    Assert.assertEquals(jc.getParameters().get(0).getDescription(),
        "Options: " + EnumSet.allOf((Class<? extends Enum>) ArgsEnum.ChoiceType.class));
  }

}


