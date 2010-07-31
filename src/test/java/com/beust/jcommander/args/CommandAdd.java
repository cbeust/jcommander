package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

import java.util.List;

public class CommandAdd {

  @Parameter
  public List<String> patterns;

  @Parameter(names = "-i")
  public Boolean interactive = false;

}
