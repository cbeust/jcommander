package com.beust.jcommander.converters;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Converter for {@link LocalTime}.
 */
public class LocalTimeConverter extends JavaTimeConverter<LocalTime> {

  public LocalTimeConverter(String optionName) {
    super(optionName, LocalTime.class);
  }

  @Override
  protected Set<DateTimeFormatter> supportedFormats() {
    return Set.of(DateTimeFormatter.ISO_LOCAL_TIME);
  }

  @Override
  protected LocalTime parse(String value, DateTimeFormatter formatter) {
    return LocalTime.parse(value, formatter);
  }
}
