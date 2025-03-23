package com.beust.jcommander.converters;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Converter to {@link Instant}.
 */
public final class InstantConverter extends JavaTimeConverter<Instant> {

  public InstantConverter(String optionName) {
    super(optionName, Instant.class);
  }

  @Override
  protected Set<DateTimeFormatter> supportedFormats() {
    return Set.of(DateTimeFormatter.ISO_INSTANT);
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
