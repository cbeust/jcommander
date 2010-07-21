package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public class LongConverter implements IStringConverter<Long> {

  @Override
  public Long convert(String value) {
    try {
      return Long.parseLong(value);
    } catch(NumberFormatException ex) {
      throw new ParameterException("Couldn't convert \"" + value + "\" to a long");
    }
  }

}
