package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;

import java.util.ArrayList;
import java.util.List;

public class Args2 {
  @Parameter(description = "List of parameters")
  public List parameters = Lists.newArrayList();

  @Parameter(names = {"-log", "-verbose"}, description = "Level of verbosity")
  public Integer verbose = 1;

  @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
  public String groups;

  @Parameter(names = "-debug", description = "Debug mode")
  public boolean debug = false;

  @Parameter(names = "-host", description = "The host")
  public List hosts = new ArrayList();
}
