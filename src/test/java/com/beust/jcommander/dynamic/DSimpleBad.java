package com.beust.jcommander.dynamic;

import com.beust.jcommander.DynamicParameter;

import java.util.List;

public class DSimpleBad {

  @DynamicParameter(names = "-D")
  public List<String> params;
}
