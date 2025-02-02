package com.beust.jcommander.converters;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Converter for {@link ZonedDateTime}.
 */
public class ZonedDateTimeConverter extends JavaTimeConverter<ZonedDateTime> {

  public ZonedDateTimeConverter(String optionName) {
    super(optionName, ZonedDateTime.class);
  }

  @Override
  protected List<DateTimeFormatter> supportedFormats() {
    return List.of(DateTimeFormatter.ISO_ZONED_DATE_TIME);
  }

  @Override
  protected ZonedDateTime parse(String value, DateTimeFormatter formatter) {
    return ZonedDateTime.parse(value, formatter);
  }
}
