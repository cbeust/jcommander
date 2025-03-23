package com.beust.jcommander.converters;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Converter for {@link ZonedDateTime}.
 */
public class ZonedDateTimeConverter extends JavaTimeConverter<ZonedDateTime> {

  public ZonedDateTimeConverter(String optionName) {
    super(optionName, ZonedDateTime.class);
  }

  @Override
  protected Set<DateTimeFormatter> supportedFormats() {
    return Set.of(DateTimeFormatter.ISO_ZONED_DATE_TIME);
  }

  @Override
  protected ZonedDateTime parse(String value, DateTimeFormatter formatter) {
    return ZonedDateTime.parse(value, formatter);
  }
}
