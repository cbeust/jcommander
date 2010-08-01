package com.beust.jcommander.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(separators = "=")
public class CommandCommit {

  @Parameter(description = "Record changes to the repository")
  public List<String> files;

  @Parameter(names = "--amend", description = "Amend")
  public Boolean amend = false;

  @Parameter(names = "--author")
  public String author;
}
