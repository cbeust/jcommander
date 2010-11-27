package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

public class Args3 {

  @Parameter(names = "--classpath", description = "The classpath")
  public String classpath;

  @Parameter(names = { "-c", "--convention" }, description = "The convention")
  public String convention;

  @Parameter(names = { "-d", "--destination" }, description = "The destination to go to")
  public String destination;

  @Parameter(names = "--configure", description = "How to configure")
  public String configure;

  @Parameter(names = "--filespec")
  public String filespec;
}
