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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author rodion
 */
public class OptionGroupPackageManagerScenario2Test {
  private static final String NL = "\n";

  private static class GeneralOptions{
    @ParametersDelegate
    public HelpOpt helpOpts = new HelpOpt();
    @Parameter(names="--pkg-root", description = "Package installation root")
    public String pkgRoot = "/tmp/pkgs";
  }

  private static class VerbosityOptions {
    @Parameter(names="-V", description = "Make output more verbose")
    public boolean verbose;
  }

  @Parameters(optionGroupName = "Filter Options", optionGroupDescription = "Options for filtering things")
  private static class FilterOptions {
    @Parameter(names = "-v", description = "Filter by version")
    public String version;
    @Parameter(names = {"-i", "--id"}, description = "Filter by package id")
    public int pkgId;
    @Parameter(names = "--name", description = "Filter by package name")
    public String pkgName;
    @Parameter(names = {"-t", "--type"}, description = "Filter by package type")
    public List<String> pkgTypes = new ArrayList<String>() {{
      add("type1");
    }};
    /*
     * Delegated parameters are included as part of this option group,
     * unless they re-declare optionGroupName to a different name.
     */
    @ParametersDelegate
    public VerbosityOptions verbosityOptions = new VerbosityOptions();
  }

  @Parameters(optionGroupName = "Terminal Options", optionGroupDescription = "Terminal related options")
  private static class TerminalOptions {
    enum TerminalType {
      ANSI, VT100, VT320, WY370
    }

    @Parameter(names = "--term-coloring")
    public TerminalType coloringForTermType = TerminalType.ANSI;
    @Parameter(names = "--no-decorations", description = "Disable terminal colouring")
    public boolean noDecorations;
    @Parameter(names = "--no-progress-bar", description = "Disable progress bars")
    public boolean noProgressBars;
  }

  @Parameters(optionGroupName = "Help Options", optionGroupDescriptionKey = "optionGroups.help")
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
    @ParametersDelegate
    public TerminalOptions termOpts = new TerminalOptions();
    @Parameter(names = "-f", description = "Answer all prompts with 'yes'")
    public boolean force;
  }

  @Parameters(commandDescription = "Show locally installed packages")
  private static class LocalPackagesCmd {
    @Parameter(description = "pkg names")
    public List<String> pkgNameFilters = new ArrayList<String>();
    @ParametersDelegate
    public HelpOpt helpOpts = new HelpOpt();
    @ParametersDelegate
    public TerminalOptions termOpts = new TerminalOptions();
  }

  @Parameters(commandDescription = "Show running instances")
  private static class InstancesCmd {
    @Parameter(description = "instance types")
    public List<String> instanceTypes = new ArrayList<String>();
  }

  private GeneralOptions generalOptions;
  private PackagesCmd pkgCmd;
  private InstallPackageCmd installCmd;
  private LocalPackagesCmd localCmd;
  private InstancesCmd instancesCmd;
  private JCommander cmd;

  @BeforeTest
  public void beforeTest() {
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("MessageBundle", new Locale("en", "US"));
    generalOptions = new GeneralOptions();
    pkgCmd = new PackagesCmd();
    installCmd = new InstallPackageCmd();
    localCmd = new LocalPackagesCmd();
    instancesCmd = new InstancesCmd();
    cmd = new JCommander(generalOptions, bundle);
    cmd.setProgramName("packageManager");
    cmd.addCommand("pkg", pkgCmd);
    cmd.addCommand("install", installCmd, "I");
    cmd.addCommand("local", localCmd);
    cmd.addCommand("instances", instancesCmd);
  }

  @Test
  public void packageCommandUsage() {
    StringBuilder cmdUsage = new StringBuilder();
    cmd.usage("pkg", cmdUsage);
    Assert.assertEquals(cmdUsage.toString(),
            "List available packages" + NL +
                    "Usage: pkg [filter options] [help options] main parameters" + NL +
                    "" + NL +
                    "Options for filtering things" + NL +
                    "  Filter Options:" + NL +
                    "    -i, --id     Filter by package id" + NL +
                    "                 Default: 0" + NL +
                    "        --name   Filter by package name" + NL +
                    "    -t, --type   Filter by package type" + NL +
                    "                 Default: [type1]" + NL +
                    "    -V           Make output more verbose" + NL +
                    "                 Default: false" + NL +
                    "    -v           Filter by version" + NL +
                    "Help related options" + NL +
                    "  Help Options:" + NL +
                    "    -h, --h   Show help" + NL +
                    "              Default: false" + NL);
  }

  @Test
  public void installCommandUsage() {
    StringBuilder cmdUsage = new StringBuilder();
    cmd.usage("install", cmdUsage);
    Assert.assertEquals(cmdUsage.toString(),
            "Install package" + NL +
                    "Usage: install(I) [options] [filter options] [help options] [terminal options]" + NL +
                    "  Options:" + NL +
                    "    -f   Answer all prompts with 'yes'" + NL +
                    "         Default: false" + NL +
                    "" + NL +
                    "Options for filtering things" + NL +
                    "  Filter Options:" + NL +
                    "    -i, --id     Filter by package id" + NL +
                    "                 Default: 0" + NL +
                    "        --name   Filter by package name" + NL +
                    "    -t, --type   Filter by package type" + NL +
                    "                 Default: [type1]" + NL +
                    "    -V           Make output more verbose" + NL +
                    "                 Default: false" + NL +
                    "    -v           Filter by version" + NL +
                    "Help related options" + NL +
                    "  Help Options:" + NL +
                    "    -h, --h   Show help" + NL +
                    "              Default: false" + NL +
                    "Terminal related options" + NL +
                    "  Terminal Options:" + NL +
                    "        --no-decorations    Disable terminal colouring" + NL +
                    "                            Default: false" + NL +
                    "        --no-progress-bar   Disable progress bars" + NL +
                    "                            Default: false" + NL +
                    "        --term-coloring     " + NL +
                    "                            Default: ANSI" + NL);
  }

  @Test
  public void mainUsage() {
    StringBuilder cmdUsage = new StringBuilder();
    cmd.usage(cmdUsage);
    Assert.assertEquals(cmdUsage.toString(),
            "Usage: packageManager [options] [help options] [command] [command options]" + NL +
                    "  Options:" + NL +
                    "        --pkg-root   Package installation root" + NL +
                    "                     Default: /tmp/pkgs" + NL +
                    "  Commands:" + NL +
                    "    pkg      List available packages" + NL +
                    "      Usage: pkg [filter options] [help options] main parameters" + NL +
                    "    install(I)      Install package" + NL +
                    "      Usage: install(I) [options] [filter options] [help options] [terminal options]" + NL +
                    "        Options:" + NL +
                    "          -f   Answer all prompts with 'yes'" + NL +
                    "               Default: false" + NL +
                    "    local      Show locally installed packages" + NL +
                    "      Usage: local [help options] [terminal options] pkg names" + NL +
                    "    instances      Show running instances" + NL +
                    "      Usage: instances instance types" + NL +
                    "" + NL +
                    "Options for filtering things" + NL +
                    "  Filter Options:" + NL +
                    "    -i, --id     Filter by package id" + NL +
                    "                 Default: 0" + NL +
                    "        --name   Filter by package name" + NL +
                    "    -t, --type   Filter by package type" + NL +
                    "                 Default: [type1]" + NL +
                    "    -V           Make output more verbose" + NL +
                    "                 Default: false" + NL +
                    "    -v           Filter by version" + NL +
                    "Help related options" + NL +
                    "  Help Options:" + NL +
                    "    -h, --h   Show help" + NL +
                    "              Default: false" + NL +
                    "Terminal related options" + NL +
                    "  Terminal Options:" + NL +
                    "        --no-decorations    Disable terminal colouring" + NL +
                    "                            Default: false" + NL +
                    "        --no-progress-bar   Disable progress bars" + NL +
                    "                            Default: false" + NL +
                    "        --term-coloring     " + NL +
                    "                            Default: ANSI" + NL);

  }
}
