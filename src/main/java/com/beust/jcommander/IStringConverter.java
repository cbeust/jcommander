package com.beust.jcommander;

/**
 * An interface that converts strings to any arbitrary type.
 * 
 * @author cbeust
 */
public interface IStringConverter<T> {
  T convert(String value);
}
