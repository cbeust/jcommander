package com.beust.jcommander;

import org.testng.collections.Lists;

import java.util.List;

public class ArgsDefault {
  @Parameter
  public List<String> parameters = Lists.newArrayList();

  @Parameter(names = "-log", description = "Level of verbosity")
  public Integer log = 1;

  @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
  public String groups;

  @Parameter(names = "-debug", description = "Debug mode")
  public boolean debug = false;

  @Parameter(names = "-long", description = "A long number")
  public long l;

}
