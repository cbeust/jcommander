package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public class IntegerConverter implements IStringConverter<Integer> {

  @Override
  public Integer convert(String value) {
    try {
      return Integer.parseInt(value);
    } catch(NumberFormatException ex) {
      throw new ParameterException("Couldn't convert \"" + value + "\" to an integer");
    }
  }

}
