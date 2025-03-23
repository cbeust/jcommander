package com.beust.jcommander.converters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Converter for {@link LocalDateTime}.
 */
public class LocalDateTimeConverter extends JavaTimeConverter<LocalDateTime> {

  public LocalDateTimeConverter(String optionName) {
    super(optionName, LocalDateTime.class);
  }

  @Override
  protected Set<DateTimeFormatter> supportedFormats() {
    return Set.of(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

  @Override
  protected LocalDateTime parse(String value, DateTimeFormatter formatter) {
    return LocalDateTime.parse(value, formatter);
  }
}
