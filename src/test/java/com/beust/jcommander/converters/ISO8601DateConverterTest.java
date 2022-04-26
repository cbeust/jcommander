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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Test
public class ISO8601DateConverterTest {

  @Test
  public void testDate() throws ParseException {
    class Arg {
      @Parameter(names = "-date", converter = ISO8601DateConverter.class)
      Date date;
    }
    String format = "dd.MM.yyyy";

    SimpleDateFormat formatter = new SimpleDateFormat(format);
    Date dateNow = formatter.parse(formatter.format(new Date()));
    Arg command = new Arg();
    JCommander jc = JCommander.newBuilder().addObject(command).build();
    jc.parse("-date", new SimpleDateFormat(format).format(dateNow));

    Assert.assertEquals(command.date, dateNow);
  }

}
