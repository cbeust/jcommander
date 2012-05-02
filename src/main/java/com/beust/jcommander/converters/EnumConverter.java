package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;

import java.util.EnumSet;

/**
 * {@link com.beust.jcommander.IStringConverter} for {@link Enum}.
 *
 * @author Julien Nicoulaud
 */
public class EnumConverter extends BaseConverter<Enum> {

  /**
   * The {@link Enum} type.
   */
  private Class<? extends Enum> type;

  public EnumConverter(String optionName, Class<? extends Enum> type) {
    super(optionName);
    this.type = type;
  }

  /**
   * {@inheritDoc}
   */
  public Enum convert(String value) {
    try {
      return Enum.valueOf(type, value.toUpperCase());
    } catch (Exception e) {
      throw new ParameterException("Invalid value for " + getOptionName() + " parameter. Allowed values:" +
          EnumSet.allOf(type));
    }
  }
}
