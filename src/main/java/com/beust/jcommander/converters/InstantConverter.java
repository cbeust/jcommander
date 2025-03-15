package com.beust.jcommander.converters;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Converter to {@link Instant}.
 */
public final class InstantConverter extends JavaTimeConverter<Instant> {

  public InstantConverter(String optionName) {
    super(optionName, Instant.class);
  }

  @Override
  protected List<DateTimeFormatter> supportedFormats() {
    return List.of(DateTimeFormatter.ISO_INSTANT);
  }

  @Override
  protected Instant parse(String value, DateTimeFormatter formatter) {
    try {
      long ms = Long.parseLong(value);
      return Instant.ofEpochMilli(ms);
    } catch (NumberFormatException e) {
      return formatter.parse(value, Instant::from);
    }
  }
}
