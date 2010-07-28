package com.beust.jcommander;

/**
 * A factory for IStringConverter. This interface lets you specify your
 * converters in one place instead of having them repeated all over
 * your argument classes.
 * 
 * @author cbeust
 */
public interface IStringConverterFactory {
  <T> Class<? extends IStringConverter<T>> getConverter(Class<T> forType);
}
