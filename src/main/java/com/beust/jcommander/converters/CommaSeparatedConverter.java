package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;

import java.util.Arrays;
import java.util.List;

public class CommaSeparatedConverter implements IStringConverter<List<String>> {

  @Override
  public List<String> convert(String value) {
    return Arrays.asList(value.split(","));
  }

}
