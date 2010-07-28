package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = ":")
public class SeparatorColon {

  @Parameter(names = "-verbose", arity = 1)
  public boolean verbose = false;
}
