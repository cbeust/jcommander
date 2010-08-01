package com.beust.jcommander.command;

import com.beust.jcommander.Parameter;

import java.util.List;

public class CommandAdd {

  @Parameter(description = "Add file contents to the index")
  public List<String> patterns;

  @Parameter(names = "-i")
  public Boolean interactive = false;

}
