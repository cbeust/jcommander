package com.beust.jcommander;

@Parameters(separators = ":")
public class SeparatorColon {

  @Parameter(names = "-verbose", arity = 1)
  public boolean verbose = false;
}
