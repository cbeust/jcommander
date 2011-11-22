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
 * TODO Modify to work with all valid ISO 8601 date formats (currently only works with yyyy-MM-dd).
 *
 * @author Angus Smithson
 */
public class ISO8601DateConverter extends BaseConverter<Date> {

  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  public ISO8601DateConverter(String optionName) {
    super(optionName);
  }

  public Date convert(String value) {
    try {
      return DATE_FORMAT.parse(value);
    } catch (ParseException pe) {
      throw new ParameterException(getErrorString(value, String.format("an ISO-8601 formatted date (%s)", DATE_FORMAT.toPattern())));
    }
  }
}
