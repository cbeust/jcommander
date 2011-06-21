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

package com.beust.jcommander.command;

import com.beust.jcommander.*;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * @author rodionmoiseev
 */
public class CommandCallableScenarioTest {
  private boolean callableCalled = false;

  @BeforeMethod
  public void clearCallableCalled() {
    callableCalled = false;
  }

  @Parameters(commandName = "cmd1", commandDescription = "Command #1")
  public final class Cmd1 {
    @Parameter
    private final List<String> parameters = Lists.newArrayList();
    @Parameter(names = "-g")
    private String groups;
    @Parameter(names = "-d", description = "Debug")
    private boolean debug;
  }

  @Parameters(commandName = "cmd2", commandAliases = "c2", commandDescription = "Command #2")
  public final class Cmd2 {
    @Parameter(names = "-g")
    private String groups;
    @Parameter(names = "-d", description = "Debug")
    private boolean debug;
  }

  @Parameters(commandName = "cmd3")
  public final class Cmd3 {
    @Parameter(names = "--groups")
    private Set<String> groups = Sets.newLinkedHashSet();
    @Parameter(names = "--id")
    private int id;
  }

  private JCommander setUpScenario() {
    CommandMain main = new CommandMain();
    JCommander jc = new JCommander(main);
    jc.addCommand(new Cmd1(), new JCallable<Cmd1>() {
      public void call(Cmd1 parsedOpts) throws Exception {
        callableCalled = true;
        assertEquals(parsedOpts.parameters, list("param1", "param2", "param3"));
        assertEquals(parsedOpts.groups, "group1,group2");
        assertFalse(parsedOpts.debug);
      }
    });
    jc.addCommand(new Cmd2(), new JCallable<Cmd2>() {
      public void call(Cmd2 parsedOpts) throws Exception {
        callableCalled = true;
        assertTrue(parsedOpts.debug);
        assertNull(parsedOpts.groups);
        throw new MyApplicationException("My exception message");
      }
    });
    final Cmd3 cmd3 = new Cmd3();
    jc.addCommand(cmd3, new Runnable() {
      public void run() {
        callableCalled = true;
        assertEquals(cmd3.groups, set("group3", "group4"));
        assertEquals(cmd3.id, 123);
      }
    });
    return jc;
  }

  @Test
  public void commandCallableScenario_uncheckedParseOfCmd1() {
    setUpScenario().parse("cmd1", "-g", "group1,group2", "param1", "param2", "param3");
    assertTrue(callableCalled);
  }

  @Test
  public void commandCallableScenario_checkedParseOfCmd2_withException() {
    try {
      setUpScenario().parseChecked("c2", "-d");
      fail("Should throw an exception");
    } catch (Exception e) {
      assertEquals(e.getMessage(), "My exception message");
    }
    assertTrue(callableCalled);
  }

  @Test
  public void commandCallableScenario_uncheckedParseOfCmd3() {
    setUpScenario().parse("cmd3", "--groups", "group3", "--groups", "group4", "--id", "123");
    assertTrue(callableCalled);
  }

  @Test(expectedExceptions = ParameterException.class,
          expectedExceptionsMessageRegExp = ".*--invalid-param.*")
  public void commandCallableScenario_uncheckedParse_parameterException(){
    setUpScenario().parse("cmd1", "--invalid-param", "value");
  }

  @Test(expectedExceptions = ParameterException.class,
          expectedExceptionsMessageRegExp = ".*--invalid-param.*")
  public void commandCallableScenario_checkedParse_parameterException() throws Exception{
    setUpScenario().parseChecked("cmd1", "--invalid-param", "value");
  }

  private static final class MyApplicationException extends Exception {
    MyApplicationException(String message) {
      super(message);
    }
  }

  private static List<String> list(String... values) {
    return Arrays.asList(values);
  }

  private static Set<String> set(String... values) {
    return new LinkedHashSet<String>(list(values));
  }
}
