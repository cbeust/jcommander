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

package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.BigDecimalConverter;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.IntegerConverter;
import com.beust.jcommander.converters.StringConverter;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class ArgsConverter {

  @Parameter(names = "-file", converter = FileConverter.class)
  public File file;

  @Parameter(names = "-listStrings", converter = StringConverter.class, list = true)
  public List<String> listStrings;

  @Parameter(names = "-listInts", converter = IntegerConverter.class, list = true)
  public List<Integer> listInts;

  @Parameter(names = "-listBigDecimals", converter = BigDecimalConverter.class, list = true)
  public List<BigDecimal> listBigDecimals;
}
