package com.beust.jcommander;

import java.io.File;

public class ArgsConverterFactory {

  @Parameter(names = "-file")
  File file;

  @Parameter(names = "-integer")
  Integer integer;
}
