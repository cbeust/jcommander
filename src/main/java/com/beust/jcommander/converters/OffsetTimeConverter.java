package com.beust.jcommander.converters;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Converter for {@link OffsetTime}.
 */
public class OffsetTimeConverter extends JavaTimeConverter<OffsetTime> {

  public OffsetTimeConverter(String optionName) {
    super(optionName, OffsetTime.class);
  }

  @Override
  protected Set<DateTimeFormatter> supportedFormats() {
    return Set.of(DateTimeFormatter.ISO_OFFSET_TIME);
  }

  @Override
  protected OffsetTime parse(String value, DateTimeFormatter formatter) {
    return OffsetTime.parse(value, formatter);
  }
}
