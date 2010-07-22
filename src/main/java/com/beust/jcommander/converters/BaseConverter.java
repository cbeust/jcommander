package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;

abstract public class BaseConverter<T> implements IStringConverter<T> {

  private String m_optionName;

  public BaseConverter(String optionName) {
    m_optionName = optionName;
  }

  public String getOptionName() {
    return m_optionName;
  }

  protected String getErrorString(String value, String to) {
    return "\"" + getOptionName() + "\": couldn't convert \"" + value + "\" to " + to;
  }

}
