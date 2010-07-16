package com.beust.jcommander;

public class ArgsPrivate {
  @Parameter(names = "-verbose")
  private Integer verbose = 1;

  public Integer getVerbose() {
    return verbose;
  }
}
