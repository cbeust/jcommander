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

package com.beust.jcommander.defaultprovider;

import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.ParameterException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A default provider that reads its default values from a property file.
 * 
 * @author cbeust
 */
public class PropertyFileDefaultProvider implements IDefaultProvider {
  public static final String DEFAULT_FILE_NAME = "jcommander.properties";
  private Properties properties;

  public PropertyFileDefaultProvider() {
    init(DEFAULT_FILE_NAME);
  }

  public PropertyFileDefaultProvider(String fileName) {
    init(fileName);
  }

  private void init(String fileName) {
    try {
      properties = new Properties();
      URL url = ClassLoader.getSystemResource(fileName);
      if (url != null) {
        properties.load(url.openStream());
      } else {
        throw new ParameterException("Could not find property file: " + fileName
            + " on the class path");
      }
    }
    catch (IOException e) {
      throw new ParameterException("Could not open property file: " + fileName);
    }
  }

  @Override
  public final List<String> getDefaultValueFor(final String optionName) {
    final List<String> result = new ArrayList<>();

    int index = 0;
    while (index < optionName.length() && ! Character.isLetterOrDigit(optionName.charAt(index))) {
      index++;
    }
    String key = optionName.substring(index);

    result.add(properties.getProperty(key));

    return result;
  }

}
