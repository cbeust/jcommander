package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

public class ArgsPassword {
  @Parameter(names = "-password", description = "Connection password", password = true)
  public String password;
}
