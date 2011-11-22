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

import com.beust.jcommander.ParameterException;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for comma-separated converters which stores the name of the option
 * and object type in case of errors.
 *
 * @author Angus Smithson
 */
abstract class CommaSeparatedBaseConverter<T> extends BaseConverter<List<T>> {

  private String m_typeDescription;

  public CommaSeparatedBaseConverter(String optionName, String typeDescription) {
    super(optionName);
    m_typeDescription = typeDescription;
  }

  public List<T> convert(String value) {
    ArrayList<T> al = new ArrayList<T>();
    final String[] values = value.split(",");
    try {
      for (String s : values) {
        al.add(getIndividualValue(s));
      }
    } catch (Throwable t) {
      throw new ParameterException(getErrorString(value,
          String.format("a list of type %s.", m_typeDescription)));
    }
    return al;
  }

  abstract T getIndividualValue(String value);
}
