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

package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;

/**
 * Base class for converters that stores the name of the option.
 * 
 * @author cbeust
 */
abstract public class BaseConverter<T> implements IStringConverter<T> {

  private String m_optionName;

  public BaseConverter(String optionName) {
    m_optionName = optionName;
  }

  public String getOptionName() {
    return m_optionName;
  }

  protected String getErrorString(String value, String to) {
    return "\"" + getOptionName() + "\": couldn't convert \"" + value + "\" to " + to;
  }

}
