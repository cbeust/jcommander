package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;

public class IntegerConverter implements IStringConverter<Integer> {

  @Override
  public Integer convert(String value) {
    return Integer.parseInt(value);
  }

}
