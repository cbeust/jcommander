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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Test
public class ISO8601LocalDateConverterTest {

  @Test
  public void testDateTime() {
    class Arg {
      @Parameter(names = "-datetime", converter = ISO8601LocalDateConverter.class)
      LocalDate date;
    }
    String format = "hhmm";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    LocalDate dateNow = LocalDate.parse(formatter.format(LocalDate.now()));
    Arg command = new Arg();
    JCommander jc = JCommander.newBuilder().addObject(command).build();
    jc.parse("-datetime", dateNow.format(DateTimeFormatter.ofPattern(format)));

    Assert.assertEquals(command.date, dateNow);
  }

}
