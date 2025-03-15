package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Base class for all {@link java.time} converters.
 *
 * @param <T> concrete type to parse into
 */
public abstract class JavaTimeConverter<T extends TemporalAccessor> extends BaseConverter<T> {

  private final Class<T> toClass;

  /**
   * Inheritor constructors should have only 1 parameter - optionName.
   *
   * @param optionName name of the option
   * @param toClass    type to parse into
   */
  protected JavaTimeConverter(String optionName, Class<T> toClass) {
    super(optionName);
    this.toClass = toClass;
  }

  @Override
  public final T convert(String value) {
    return supportedFormats().stream()
            .map(new Mapper(value))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new ParameterException(errorMessage(value)));
  }

  /**
   * Supported formats for this type, e.g. {@code HH:mm:ss}
   *
   * @return list of supported formats
   */
  protected abstract List<DateTimeFormatter> supportedFormats();

  /**
   * Parse the value using the specified formatter.
   *
   * @param value value to parse
   * @param formatter formatter specifying supported format
   * @return parsed value
   */
  protected abstract T parse(String value, DateTimeFormatter formatter);

  private String errorMessage(String value) {
    return getErrorString(value, "a " + toClass.getSimpleName());
  }

  private final class Mapper implements Function<DateTimeFormatter, T> {

    private final String value;

    private Mapper(String value) {
      this.value = value;
    }

    @Override
    public T apply(DateTimeFormatter formatter) {
      try {
        return parse(value, formatter);
      } catch (DateTimeParseException exc) {
        return null;
      } catch (Exception exc) {
        throw new ParameterException(errorMessage(value), exc);
      }
    }
  }
}
