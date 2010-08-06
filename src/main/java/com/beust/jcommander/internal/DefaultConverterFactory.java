/**
 * Copyright (C) 2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beust.jcommander.internal;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IStringConverterFactory;
import com.beust.jcommander.converters.BooleanConverter;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.IntegerConverter;
import com.beust.jcommander.converters.LongConverter;
import com.beust.jcommander.converters.StringConverter;

import java.io.File;
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
    m_classConverters.put(File.class, FileConverter.class);
  }

  public Class<? extends IStringConverter<?>> getConverter(Class forType) {
    return m_classConverters.get(forType);
  }

}
