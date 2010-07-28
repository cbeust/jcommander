package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ResourceBundle;

@ResourceBundle("MessageBundle")
public class ArgsI18N2 {

  @Parameter(names = "-host", description = "Host", descriptionKey = "host")
  String hostName;
}
