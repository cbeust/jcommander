package com.beust.jcommander.args;

import com.beust.jcommander.HostPort;
import com.beust.jcommander.Parameter;

public class ArgsConverterFactory {

  @Parameter(names = "-hostport")
  public HostPort hostPort;
}
