package com.beust.jcommander.converters;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Converter for {@link LocalTime}.
 */
public class LocalTimeConverter extends JavaTimeConverter<LocalTime> {

  public LocalTimeConverter(String optionName) {
    super(optionName, LocalTime.class);
  }

  @Override
  protected List<DateTimeFormatter> supportedFormats() {
    return List.of(DateTimeFormatter.ISO_LOCAL_TIME);
  }

  @Override
  protected LocalTime parse(String value, DateTimeFormatter formatter) {
    return LocalTime.parse(value, formatter);
  }
}
