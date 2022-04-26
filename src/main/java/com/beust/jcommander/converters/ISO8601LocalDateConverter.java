/**
 * Copyright (C) 2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ISO8601LocalDateConverter extends DateConverter<LocalDate> {

  public ISO8601LocalDateConverter(String optionName) {
    super(optionName);
  }

  public LocalDate convert(String value) {
    for (String pattern : DATE_FORMAT_LIST) {
      try {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
      } catch (DateTimeParseException ignored) {
        continue;
      }
    }
    throw new ParameterException(getErrorString(value, "an ISO-8601 formatted time"));
  }
}
