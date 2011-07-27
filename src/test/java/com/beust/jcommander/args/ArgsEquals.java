package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class ArgsEquals {

  @Parameter(names = "-args")
  public String args;
}
