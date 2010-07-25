package com.beust.jcommander;

@Parameters(separators = "=")
public class SeparatorEqual {

  @Parameter(names = "-log")
  public Integer log = 2;

  @Parameter(names = "--longoption")
  public Integer longOption;
}
