package com.beust.jcommander;

public class ArgsPassword {
  @Parameter(names = "-password", description = "Connection password", password = true)
  public String password;
}
