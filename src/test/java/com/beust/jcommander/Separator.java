package com.beust.jcommander;

@Parameters(separators = "=")
public class Separator {

  @Parameter(names = "-log")
  public Integer log = 2;
}
