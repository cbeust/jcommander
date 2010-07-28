package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(optionPrefixes = "/")
public class SlashSeparator {

  @Parameter(names = "/verbose")
  public boolean verbose = false;

  @Parameter(names = "/file")
  public String file;
}
