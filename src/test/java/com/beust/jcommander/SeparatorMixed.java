package com.beust.jcommander;

@Parameters(separators = ":=")
public class SeparatorMixed {

  @Parameter(names = "-level")
  public Integer level = 0;

  @Parameter(names = "-long")
  public Long l = 0l;
}
