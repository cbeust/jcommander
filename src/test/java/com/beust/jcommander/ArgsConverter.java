package com.beust.jcommander;

import com.beust.jcommander.converters.CommaSeparatedConverter;
import com.beust.jcommander.converters.FileConverter;

import java.io.File;
import java.util.List;

public class ArgsConverter {

  @Parameter(names = "-file", converter = FileConverter.class)
  File file;

  @Parameter(names = "-days", converter = CommaSeparatedConverter.class)
  List<String> days;
}
