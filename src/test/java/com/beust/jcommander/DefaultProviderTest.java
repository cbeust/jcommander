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

package com.beust.jcommander;

import com.beust.jcommander.args.ArgsDefault;
import com.beust.jcommander.defaultprovider.PropertyFileDefaultProvider;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DefaultProviderTest {
  private static final IDefaultProvider DEFAULT_PROVIDER = new IDefaultProvider() {

    public String getDefaultValueFor(String optionName) {
      return "-debug".equals(optionName) ? "false" : "42";
    }
    
  };

  private ArgsDefault defaultProvider(IDefaultProvider provider, String... args) {
    ArgsDefault a = new ArgsDefault();
    JCommander jc = new JCommander(a);
    jc.setDefaultProvider(provider);

    jc.parse(args);
    return a;
  }

  @Test
  public void defaultProvider1() {
    ArgsDefault a = defaultProvider(DEFAULT_PROVIDER, "f");

    Assert.assertEquals(a.groups, "42");
    Assert.assertEquals(a.level, 42);
    Assert.assertEquals(a.log.intValue(), 42);
  }

  @Test
  public void defaultProvider2() {
    ArgsDefault a = defaultProvider(DEFAULT_PROVIDER, "-groups", "foo", "f");

    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 42);
    Assert.assertEquals(a.log.intValue(), 42);
  }

  @Test
  public void defaultProvider3() {
    ArgsDefault a = defaultProvider(DEFAULT_PROVIDER, "-groups", "foo", "-level", "13", "f");

    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 13);
    Assert.assertEquals(a.log.intValue(), 42);
  }

  @Test
  public void defaultProvider4() {
    ArgsDefault a = defaultProvider(DEFAULT_PROVIDER,
        "-log", "19", "-groups", "foo", "-level", "13", "f");

    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 13);
    Assert.assertEquals(a.log.intValue(), 19);
  }

  @Test
  public void propertyFileDefaultProvider1() {
    ArgsDefault a = defaultProvider(new PropertyFileDefaultProvider(), "f");

    Assert.assertEquals(a.groups, "unit");
    Assert.assertEquals(a.level, 17);
    Assert.assertEquals(a.log.intValue(), 18);
  }

  @Test
  public void propertyFileDefaultProvider2() {
    ArgsDefault a = defaultProvider(new PropertyFileDefaultProvider(), "-groups", "foo", "f");
    
    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 17);
    Assert.assertEquals(a.log.intValue(), 18);
  }

  @Test
  public void propertyFileDefaultProvider3() {
    ArgsDefault a = defaultProvider(new PropertyFileDefaultProvider(),
        "-groups", "foo", "-level", "13", "f");

    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 13);
    Assert.assertEquals(a.log.intValue(), 18);
  }

  @Test
  public void propertyFileDefaultProvider4() {
    ArgsDefault a = defaultProvider(new PropertyFileDefaultProvider(),
        "-log", "19", "-groups", "foo", "-level", "13", "f");

    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 13);
    Assert.assertEquals(a.log.intValue(), 19);
  }

  @Test
  public void missingRequiredParameterWithDefaultValueProviderShouldNotRaiseParameterException() {
    class ArgsRequired {
      @Parameter(names = "-log", description = "Level of verbosity", required = true)
      public Integer log;
    }

    IDefaultProvider defaultProvider = new IDefaultProvider() {
      public String getDefaultValueFor(String optionName) {
        return "-log".equals(optionName) ? "1" : "";
      }
    };

    ArgsRequired a = new ArgsRequired();
    JCommander jc = new JCommander(a);
    jc.setDefaultProvider(defaultProvider);
    jc.parse();

    Assert.assertEquals(a.log.intValue(), 1);
  }
}
