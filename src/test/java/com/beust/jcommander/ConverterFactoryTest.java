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

import com.beust.jcommander.args.ArgsConverterFactory;
import com.beust.jcommander.args.ArgsMainParameter1;
import com.beust.jcommander.args.ArgsMainParameter2;
import com.beust.jcommander.args.IHostPorts;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test the converter factory feature.
 * 
 * @author cbeust
 */
public class ConverterFactoryTest {
  private static final Map<Class, Class<? extends IStringConverter<?>>> MAP = new HashMap() {{
    put(HostPort.class, HostPortConverter.class);
  }};

  private static final IStringConverterFactory CONVERTER_FACTORY = new IStringConverterFactory() {

    public Class<? extends IStringConverter<?>> getConverter(Class forType) {
      return MAP.get(forType);
    }
    
  };

  @Test
  public void parameterWithHostPortParameters() {
    ArgsConverterFactory a = new ArgsConverterFactory();
    JCommander jc = new JCommander(a);
    jc.addConverterFactory(CONVERTER_FACTORY);
    jc.parse("-hostport", "example.com:8080");

    Assert.assertEquals(a.hostPort.host, "example.com");
    Assert.assertEquals(a.hostPort.port.intValue(), 8080);
  }

  /**
   * Test that main parameters can be used with string converters,
   * either with a factory or from the annotation.
   */
  private void mainWithHostPortParameters(IStringConverterFactory f, IStringConverterInstanceFactory f2, IHostPorts a) {
    JCommander jc = new JCommander(a);
    if (f != null) jc.addConverterFactory(f);
    if (f2 != null) jc.addConverterInstanceFactory(f2);
    jc.parse("a.com:10", "b.com:20");
    Assert.assertEquals(a.getHostPorts().get(0).host, "a.com");
    Assert.assertEquals(a.getHostPorts().get(0).port.intValue(), 10);
    Assert.assertEquals(a.getHostPorts().get(1).host, "b.com");
    Assert.assertEquals(a.getHostPorts().get(1).port.intValue(), 20);
  }

  @Test
  public void mainWithoutFactory() {
    mainWithHostPortParameters(null, null, new ArgsMainParameter2());
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void mainWithoutConverterWithoutFactory() {
    mainWithHostPortParameters(null, null, new ArgsMainParameter1());
  }

  @Test
  public void mainWithFactory() {
    mainWithHostPortParameters(CONVERTER_FACTORY, null, new ArgsMainParameter1());
  }

  @Test
  public void mainWithInstanceFactory() {
    mainWithHostPortParameters(null, new IStringConverterInstanceFactory() {
      @Override
      public IStringConverter<?> getConverterInstance(Parameter parameter, Class<?> forType, String optionName) {
        return HostPort.class.equals(forType) ? new HostPortConverter() : null;
      }
    }, new ArgsMainParameter1());
  }

}

