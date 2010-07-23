package com.beust.jcommander;

@Parameters(resourceBundle = "MessageBundle")
public class ArgsI18N2New {

  @Parameter(names = "-host", description = "Host", descriptionKey = "host")
  String hostName;
}
