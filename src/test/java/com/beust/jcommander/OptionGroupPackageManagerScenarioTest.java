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
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rodion
 */
public class OptionGroupPackageManagerScenarioTest {
  private static final String NL = "\n";

  @Parameters(optionGroupName = "Filter Options", optionGroupDescription = "Options for filtering things")
  private static class FilterOptions {
    @Parameter(names = "-v", description = "Filter by version")
    public String version;
    @Parameter(names = {"-i", "--id"}, description = "Filter by package id")
    public int pkgId;
  }

  private static class HelpOpt {
    @Parameter(names = {"-h", "--h"}, description = "Show help")
    public boolean showHelp;
  }

  @Parameters(commandDescription = "List available packages")
  private static class PackagesCmd {
    @Parameter(description = "main parameters")
    public List<String> mainParams = new ArrayList<String>();
    @ParametersDelegate
    public FilterOptions filterOpts = new FilterOptions();
    @ParametersDelegate
    public HelpOpt helpOpts = new HelpOpt();
  }

  @Parameters(commandDescription = "Install package")
  private static class InstallPackageCmd {
    @ParametersDelegate
    public FilterOptions filterOpts = new FilterOptions();
    @ParametersDelegate
    public HelpOpt helpOpts = new HelpOpt();
    @Parameter(names = "-f", description = "Answer all prompts with 'yes'")
    public boolean force;
  }

  private PackagesCmd pkgCmd;
  private InstallPackageCmd installCmd;
  private JCommander cmd;

  @BeforeTest
  public void beforeTest() {
    pkgCmd = new PackagesCmd();
    installCmd = new InstallPackageCmd();
    cmd = new JCommander();
    cmd.setProgramName("packageManager");
    cmd.addCommand("pkg", pkgCmd);
    cmd.addCommand("install", installCmd);
  }

  @Test
  public void packageCommandUsage() {
    StringBuilder cmdUsage = new StringBuilder();
    cmd.usage("pkg", cmdUsage);
    Assert.assertEquals( cmdUsage.toString(),
            "List available packages" + NL +
                    "Usage: pkg [options] [filter options] main parameters" + NL +
                    "  Options:" + NL +
                    "    -h, --h   Show help" + NL +
                    "              Default: false" + NL +
                    "" + NL +
                    "Options for filtering things" + NL +
                    "  Filter Options:" + NL +
                    "    -i, --id   Filter by package id" + NL +
                    "               Default: 0" + NL +
                    "    -v         Filter by version" + NL);
  }

  @Test
  public void installCommandUsage() {
    StringBuilder cmdUsage = new StringBuilder();
    cmd.usage("install", cmdUsage);
    Assert.assertEquals(cmdUsage.toString(),
            "Install package" + NL +
                    "Usage: install [options] [filter options]" + NL +
                    "  Options:" + NL +
                    "    -h, --h   Show help" + NL +
                    "              Default: false" + NL +
                    "    -f        Answer all prompts with 'yes'" + NL +
                    "              Default: false" + NL +
                    "" + NL +
                    "Options for filtering things" + NL +
                    "  Filter Options:" + NL +
                    "    -i, --id   Filter by package id" + NL +
                    "               Default: 0" + NL +
                    "    -v         Filter by version" + NL);
  }

  @Test
  public void mainUsage() {
    StringBuilder cmdUsage = new StringBuilder();
    cmd.usage(cmdUsage);
    Assert.assertEquals(cmdUsage.toString(),
            "Usage: packageManager [command] [command options]" + NL +
                    "  Commands:" + NL +
                    "    pkg      List available packages" + NL +
                    "      Usage: pkg [options] [filter options] main parameters" + NL +
                    "        Options:" + NL +
                    "          -h, --h   Show help" + NL +
                    "                    Default: false" + NL +
                    "    install      Install package" + NL +
                    "      Usage: install [options] [filter options]" + NL +
                    "        Options:" + NL +
                    "          -h, --h   Show help" + NL +
                    "                    Default: false" + NL +
                    "          -f        Answer all prompts with 'yes'" + NL +
                    "                    Default: false" + NL +
                    "" + NL +
                    "Options for filtering things" + NL +
                    "  Filter Options:" + NL +
                    "    -i, --id   Filter by package id" + NL +
                    "               Default: 0" + NL +
                    "    -v         Filter by version" + NL);
  }
}
