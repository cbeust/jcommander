package com.beust.jcommander.converters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

/**
 * Converter for {@link LocalDateTime}.
 */
public class LocalDateTimeConverter extends JavaTimeConverter<LocalDateTime> {

  public LocalDateTimeConverter(String optionName) {
    super(optionName, LocalDateTime.class);
  }

  @Override
  protected List<DateTimeFormatter> supportedFormats() {
    return List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").appendLiteral('T').append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter()
    );
  }

  @Override
  protected LocalDateTime parse(String value, DateTimeFormatter formatter) {
    return LocalDateTime.parse(value, formatter);
  }
}
