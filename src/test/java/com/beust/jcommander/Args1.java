package com.beust.jcommander;

import org.testng.collections.Lists;

import java.util.List;

public class Args1 {
  @Parameter
  public List<String> parameters = Lists.newArrayList();

  @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity", required = true)
  public Integer verbose = 1;

  @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
  public String groups;

  @Parameter(names = "-debug", description = "Debug mode")
  public boolean debug = false;

  @Parameter(names = "-long", description = "A long number")
  public long l;
}
