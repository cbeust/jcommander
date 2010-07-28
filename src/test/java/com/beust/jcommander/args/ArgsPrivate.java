package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

public class ArgsPrivate {
  @Parameter(names = "-verbose")
  private Integer verbose = 1;

  public Integer getVerbose() {
    return verbose;
  }
}
