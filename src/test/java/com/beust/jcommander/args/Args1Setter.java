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
import com.beust.jcommander.internal.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Args1Setter {
  @Parameter
  public void setParameters(List<String> p) {
    parameters = p;
  }

  public List<String> getParameters() {
    return this.parameters;
  }
  public List<String> parameters = Lists.newArrayList();

  @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity", required = true)
  public void setVerbose(Integer v) {
    verbose = v;
  }
  public Integer verbose = 1;

  @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
  public void setGroups(String g) {
    groups = g;
  }

  public String groups;

  @Parameter(names = "-debug", description = "Debug mode")
  public void setDebug(boolean d) {
    debug = d;
  }

  public boolean debug = false;

  @Parameter(names = "-long", description = "A long number")
  public void setLong(long ll) {
    l = ll;
  }

  public long l;

  @Parameter(names = "-double", description = "A double number")
  public void setDouble(double d) {
    doub = d;
  }

  public double doub;

  @Parameter(names = "-float", description = "A float number")
  public void setFloat(float f) {
    floa = f;
  }

  public float floa;

  @Parameter(names = "-bigdecimal", description = "A BigDecimal number")
  public void setBigDecimal(BigDecimal bd) {
    bigd = bd;
  }

  public BigDecimal bigd;

  @Parameter(names = "-date", description = "An ISO 8601 formatted date.")
  public void setDate(Date d) {
    date = d;
  }

  public Date date;
}
