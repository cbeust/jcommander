package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;

public class ArgsValidate1 {

  @Parameter(names = "-age", validateWith = PositiveInteger.class)
  public Integer age;
}
