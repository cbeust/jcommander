package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;

public class LongConverter implements IStringConverter<Long> {

  @Override
  public Long convert(String value) {
    return Long.parseLong(value);
  }

}
