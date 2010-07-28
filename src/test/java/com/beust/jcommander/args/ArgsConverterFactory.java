package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

import java.io.File;

public class ArgsConverterFactory {

  @Parameter(names = "-file")
  public File file;

  @Parameter(names = "-integer")
  Integer integer;
}
