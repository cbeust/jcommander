package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

public class ArgsBooleanArity {
  @Parameter(names = "-debug", arity = 1)
  public Boolean debug = false;
}
