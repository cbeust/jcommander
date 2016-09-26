package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.internal.Lists;

import java.util.List;

/**
 * A converter to obtain a list of elements.
 * @param <T> the element type
 * @author simon04
 */
public class DefaultListConverter<T> implements IStringConverter<List<T>> {

  private final IParameterSplitter splitter;
  private final IStringConverter<T> converter;

  /**
   * Constructs a new converter.
   * @param splitter to split value into list of arguments
   * @param converter to convert list of arguments to target element type
   */
  public DefaultListConverter(IParameterSplitter splitter, IStringConverter<T> converter) {
    this.splitter = splitter;
    this.converter = converter;
  }

  @Override
  public List<T> convert(String value) {
    List<T> result = Lists.newArrayList();
    for (String param : splitter.split(value)) {
      result.add(converter.convert(param));
    }
    return result;
  }
}
