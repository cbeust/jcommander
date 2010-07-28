package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaSeparatedConverter;
import com.beust.jcommander.converters.FileConverter;

import java.io.File;
import java.util.List;

public class ArgsConverter {

  @Parameter(names = "-file", converter = FileConverter.class)
  public File file;

  @Parameter(names = "-days", converter = CommaSeparatedConverter.class)
  public List<String> days;
}
