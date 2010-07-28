package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = ":=")
public class SeparatorMixed {

  @Parameter(names = "-level")
  public Integer level = 0;

  @Parameter(names = "-long")
  public Long l = 0l;
}
