package com.beust.jcommander.internal;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IStringConverterFactory;
import com.beust.jcommander.converters.BooleanConverter;
import com.beust.jcommander.converters.IntegerConverter;
import com.beust.jcommander.converters.LongConverter;
import com.beust.jcommander.converters.StringConverter;

import java.util.Map;

public class DefaultConverterFactory implements IStringConverterFactory {
  /**
   * A map of converters per class.
   */
  private static Map<Class, Class<? extends IStringConverter<?>>> m_classConverters;
  
  static {
    m_classConverters = Maps.newHashMap();
    m_classConverters.put(String.class, StringConverter.class);
    m_classConverters.put(Integer.class, IntegerConverter.class);
    m_classConverters.put(int.class, IntegerConverter.class);
    m_classConverters.put(Long.class, LongConverter.class);
    m_classConverters.put(long.class, LongConverter.class);
    m_classConverters.put(Boolean.class, BooleanConverter.class);
    m_classConverters.put(boolean.class, BooleanConverter.class);
  }

  public Class<? extends IStringConverter<?>> getConverter(Class forType) {
    return m_classConverters.get(forType);
  }

}
