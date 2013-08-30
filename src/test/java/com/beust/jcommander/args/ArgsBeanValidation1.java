package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;

import javax.validation.constraints.Min;

public class ArgsBeanValidation1 {

  @Parameter(names = "-age")
  @Min(0)
  public Integer age;
}
