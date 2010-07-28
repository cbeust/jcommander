package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class SeparatorEqual {

  @Parameter(names = "-log")
  public Integer log = 2;

  @Parameter(names = "--longoption")
  public Integer longOption;
}
