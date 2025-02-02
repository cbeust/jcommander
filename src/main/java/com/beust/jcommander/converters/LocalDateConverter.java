package com.beust.jcommander.converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Converter to {@link LocalDate}.
 */
public class LocalDateConverter extends JavaTimeConverter<LocalDate> {

  public LocalDateConverter(String optionName) {
    super(optionName, LocalDate.class);
  }

  @Override
  protected List<DateTimeFormatter> supportedFormats() {
    return List.of(DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
  }

  @Override
  protected LocalDate parse(String value, DateTimeFormatter formatter) {
    return LocalDate.parse(value, formatter);
  }
}
