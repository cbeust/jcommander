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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Locale;

/**
 * @author rodion
 */
public class UsageTest {
  private static final String NL = "\n";
  private StringBuilder sb = null;

  @BeforeMethod
  public void beforeMethod() {
    sb = new StringBuilder();
  }

  @Test
  public void emptyUsage() {
    new JCommander().usage(sb);
    Assert.assertEquals(sb.toString(), "Usage: <main class>" + NL);
  }

  @Test
  public void usageWithBasicArguments() {
    class SimpleUsage {
      @Parameter(names = "-h", description = "debug option")
      public boolean debug;
      @Parameter(names = "-v")
      public String version;
    }
    JCommander cmd = new JCommander(new SimpleUsage());
    cmd.setProgramName("prog");
    cmd.usage(sb);
    Assert.assertEquals(sb.toString(),
            "Usage: prog [options]" + NL +
                    "  Options:" + NL +
                    "    -h   debug option" + NL +
                    "         Default: false" + NL +
                    "    -v   " + NL);
  }

  @Test
  public void usageOnlyWithMainArguments() {
    class SimpleUsage {
      @Parameter(description = "main args")
      public List<String> mainArgs;
    }
    JCommander cmd = new JCommander(new SimpleUsage());
    cmd.setProgramName("prog");
    cmd.usage(sb);
    Assert.assertEquals(sb.toString(),
            "Usage: prog main args" + NL);
  }

  @Test
  public void usageBasicArgumentsAndMainParams() {
    class SimpleUsage {
      @Parameter(description = "main args")
      public List<String> mainArgs;
      @Parameter(names = "-h", description = "debug option")
      public boolean debug;
    }
    JCommander cmd = new JCommander(new SimpleUsage());
    cmd.setProgramName("prog");
    cmd.usage(sb);
    Assert.assertEquals(sb.toString(),
            "Usage: prog [options] main args" + NL +
                    "  Options:" + NL +
                    "    -h   debug option" + NL +
                    "         Default: false" + NL);
  }

  @Test
  public void usageWithCommandsOnly() {
    @Parameters(commandDescription = "cmd1 description")
    class Cmd1 {
      @Parameter(names = {"-d", "--debug"}, description = "enable debug")
      public boolean debug;
    }
    class Cmd2 {
      @Parameter(names = {"-h", "--help"}, description = "show help")
      public boolean debug;
    }
    JCommander cmd = new JCommander();
    cmd.addCommand("cmd1", new Cmd1());
    cmd.addCommand("cmd2", new Cmd2());
    cmd.usage(sb);
    Assert.assertEquals(sb.toString(), "Usage: <main class> [command] [command options]" + NL +
            "  Commands:" + NL +
            "    cmd1      cmd1 description" + NL +
            "      Usage: cmd1 [options]" + NL +
            "        Options:" + NL +
            "          -d, --debug   enable debug" + NL +
            "                        Default: false" + NL +
            "    cmd2" + NL +
            "      Usage: cmd2 [options]" + NL +
            "        Options:" + NL +
            "          -h, --help   show help" + NL +
            "                       Default: false" + NL);
  }

  @Test
  public void usageWithPrettyMuchEverything() {
    @Parameters(commandDescription = "cmd1 description")
    class Cmd1 {
      @Parameter(description = "cmd1 main params")
      public List<String> mainParams;
      @Parameter(names = {"-d", "--debug"}, description = "enable debug")
      public boolean debug;
    }
    @Parameters(commandDescription = "cmd2 description")
    class Cmd2 {
      @Parameter
      public List<String> mainParams;
      @Parameter(names = {"-h", "--help"}, description = "show help")
      public boolean debug;
    }
    class Main {
      @Parameter(description = "the main params")
      public List<String> mainParams;
      @Parameter(names = "+X", description = "display extra options")
      public boolean extraOptions = true;
    }
    JCommander cmd = new JCommander(new Main());
    cmd.setProgramName("theProg");
    cmd.addCommand("cmd1", new Cmd1(), "a", "bc", "def");
    cmd.addCommand("cmd2", new Cmd2(), "ghk");
    cmd.usage(sb);
    Assert.assertEquals(sb.toString(),
            "Usage: theProg [options] [command] [command options] the main params" + NL +
                    "  Options:" + NL +
                    "    +X   display extra options" + NL +
                    "         Default: true" + NL +
                    "  Commands:" + NL +
                    "    cmd1(a,bc,def)      cmd1 description" + NL +
                    "      Usage: cmd1(a,bc,def) [options] cmd1 main params" + NL +
                    "        Options:" + NL +
                    "          -d, --debug   enable debug" + NL +
                    "                        Default: false" + NL +
                    "    cmd2(ghk)      cmd2 description" + NL +
                    "      Usage: cmd2(ghk) [options]" + NL +
                    "        Options:" + NL +
                    "          -h, --help   show help" + NL +
                    "                       Default: false" + NL);
  }

  @Test
  public void commandI18N() {
    @Parameters(commandDescriptionKey = "commands.testCmd")
    class TestCmd {
      @Parameter(names = "-d", descriptionKey = "commands.testCmd.debug")
      public boolean debug;
    }
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("MessageBundle", new Locale("en", "US"));
    JCommander cmd = new JCommander();
    cmd.setDescriptionsBundle(bundle);
    cmd.addCommand("test", new TestCmd());
    cmd.usage(sb);
    Assert.assertEquals(sb.toString(),
            "Usage: <main class> [command] [command options]" + NL +
                    "  Commands:" + NL +
                    "    test      Test command" + NL +
                    "      Usage: test [options]" + NL +
                    "        Options:" + NL +
                    "          -d   Enable debugging" + NL +
                    "               Default: false" + NL);
  }
}
