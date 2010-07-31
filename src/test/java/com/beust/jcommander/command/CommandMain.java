package com.beust.jcommander.command;

import com.beust.jcommander.Parameter;

public class CommandMain {

  @Parameter(names = "-v")
  public Boolean verbose = false;
}
