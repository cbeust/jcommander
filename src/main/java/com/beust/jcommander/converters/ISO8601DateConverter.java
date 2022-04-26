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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converts a String to a Date.
 *
 * @author Angus Smithson
 */
public class ISO8601DateConverter extends DateConverter<Date> {

  public ISO8601DateConverter(String optionName) {
    super(optionName);
  }

  public Date convert(String value) {
    for (String format : DATE_FORMAT_LIST) {
      try {
        return new SimpleDateFormat(format).parse(value);
      } catch (ParseException ignored) {
        continue;
      }
    }
    throw new ParameterException(getErrorString(value, "an ISO-8601 formatted date"));
  }

}
