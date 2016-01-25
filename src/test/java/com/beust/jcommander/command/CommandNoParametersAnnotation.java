package com.beust.jcommander.command;

import com.beust.jcommander.Parameter;
import java.util.List;

public class CommandNoParametersAnnotation {
  @Parameter(description = "Patterns of files to be added")
  public List<String> patterns;
}
