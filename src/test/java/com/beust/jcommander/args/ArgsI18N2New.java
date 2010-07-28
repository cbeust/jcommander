package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(resourceBundle = "MessageBundle")
public class ArgsI18N2New {

  @Parameter(names = "-host", description = "Host", descriptionKey = "host")
  String hostName;
}
