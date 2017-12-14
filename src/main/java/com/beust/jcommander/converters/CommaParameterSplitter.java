package com.beust.jcommander.converters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommaParameterSplitter implements IParameterSplitter {

  public List<String> split(String value) {
    if ("".equals(value)) {
      return Collections.emptyList();
    }
    return Arrays.asList(value.split(","));
  }
}
