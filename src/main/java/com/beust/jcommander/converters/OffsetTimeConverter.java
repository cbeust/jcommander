package com.beust.jcommander.converters;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Converter for {@link OffsetTime}.
 */
public class OffsetTimeConverter extends JavaTimeConverter<OffsetTime> {

  public OffsetTimeConverter(String optionName) {
    super(optionName, OffsetTime.class);
  }

  @Override
  protected List<DateTimeFormatter> supportedFormats() {
    return List.of(DateTimeFormatter.ISO_OFFSET_TIME);
  }

  @Override
  protected OffsetTime parse(String value, DateTimeFormatter formatter) {
    return OffsetTime.parse(value, formatter);
  }
}
