package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

public class ArgsLongDescription {

  @Parameter(names = "--classpath", description = "The classpath. This is a very long "
      + "description in order to test the line wrapping. Let's see how this works."
      + "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor"
      + " incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis "
      + "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
  public String classpath = "/tmp";

  @Parameter(names = { "-c", "--convention" }, description = "The convention", required = true)
  public String convention = "Java";

  @Parameter(names = { "-d", "--destination" }, description = "The destination to go to")
  public String destination;

  @Parameter(names = "--configure", description = "How to configure")
  public String configure;

  @Parameter(names = "--filespec")
  public String filespec;
}
