package com.beust.jcommander.converters;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Converter for {@link OffsetDateTime}.
 */
public class OffsetDateTimeConverter extends JavaTimeConverter<OffsetDateTime> {

  public OffsetDateTimeConverter(String optionName) {
    super(optionName, OffsetDateTime.class);
  }

  @Override
  protected List<DateTimeFormatter> supportedFormats() {
    return List.of(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }

  @Override
  protected OffsetDateTime parse(String value, DateTimeFormatter formatter) {
    return OffsetDateTime.parse(value, formatter);
  }
}
