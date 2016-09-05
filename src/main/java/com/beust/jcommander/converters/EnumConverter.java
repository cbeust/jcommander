package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import java.util.EnumSet;

/**
 * A converter to parse enums
 * @param <T> the enum type
 * @author simon04
 */
public class EnumConverter<T extends Enum<T>> implements IStringConverter<T> {

  private final String optionName;
  private final Class<T> clazz;

  /**
   * Constructs a new converter.
   * @param optionName the option name for error reporting
   * @param clazz the enum class
   */
  public EnumConverter(String optionName, Class<T> clazz) {
    this.optionName = optionName;
    this.clazz = clazz;
  }

  @Override
  public T convert(String value) {
    try {
      try {
        return Enum.valueOf(clazz, value);
      } catch (IllegalArgumentException e) {
        return Enum.valueOf(clazz, value.toUpperCase());
      }
    } catch (Exception e) {
      throw new ParameterException("Invalid value for " + optionName + " parameter. Allowed values:" +
          EnumSet.allOf(clazz));

    }
  }
}
