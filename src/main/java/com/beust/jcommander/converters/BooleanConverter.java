package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;

public class BooleanConverter extends BaseConverter<Boolean> {

  public BooleanConverter(String optionName) {
    super(optionName);
  }

  @Override
  public Boolean convert(String value) {
    if ("false".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
      return Boolean.parseBoolean(value);
    } else {
      throw new ParameterException(getErrorString(value, "a boolean"));
    }
  }

}
