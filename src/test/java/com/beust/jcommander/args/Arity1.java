package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

public class Arity1
{
  @Parameter(arity = 1, names = "-inspect", description = "", required = false)
  public boolean inspect;
}
