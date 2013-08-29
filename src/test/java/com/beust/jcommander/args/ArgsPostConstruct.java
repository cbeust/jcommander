package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

import javax.annotation.PostConstruct;

public class ArgsPostConstruct
{
  @Parameter(names = "-param1")
  public String param1;

  @Parameter(names = "-param2")
  public String param2;

  public String concatParam;

  @PostConstruct
  private void init() {
    concatParam = param1 + param2;
  }
}
