package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;

public class BooleanConverter implements IStringConverter<Boolean> {

  @Override
  public Boolean convert(String value) {
    return Boolean.parseBoolean(value);
  }

}
