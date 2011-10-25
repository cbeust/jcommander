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

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;

import org.testng.collections.Lists;

import com.beust.jcommander.Parameter;

public class Args1 {
  @Parameter
  public List<String> parameters = Lists.newArrayList();

  @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity", required = true)
  public Integer verbose = 1;

  @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
  public String groups;

  @Parameter(names = "-debug", description = "Debug mode")
  public boolean debug = false;

  @Parameter(names = "-long", description = "A long number")
  public long l;

  @Parameter(names = "-double", description = "A double number")
  public double doub;

  @Parameter(names = "-float", description = "A float number")
  public float floa;

  @Parameter(names = "-bigdecimal", description = "A BigDecimal number")
  public BigDecimal bigd;

  @Parameter(names = "-date", description = "An ISO 8601 formatted date.")
  public Date date;
}
