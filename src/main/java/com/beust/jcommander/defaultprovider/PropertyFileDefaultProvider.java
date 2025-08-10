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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.Properties;

/**
 * A default provider that reads its default values from a property file.
 * 
 * @author cbeust
 */
public class PropertyFileDefaultProvider implements IDefaultProvider {
  public static final String DEFAULT_FILE_NAME = "jcommander.properties";
  private Properties properties = new Properties();
  private static final Function<String, String> DEFAULT_OPTION_NAME_TRANSFORMER = optionName -> {
    int index = 0;
    while (index < optionName.length() && ! Character.isLetterOrDigit(optionName.charAt(index)))
      index++;
    return optionName.substring(index);
  };
  private Function<String, String> optionNameTransformer = DEFAULT_OPTION_NAME_TRANSFORMER;

  public PropertyFileDefaultProvider() {
    init(DEFAULT_FILE_NAME);
  }

  public PropertyFileDefaultProvider(String fileName) {
    init(fileName);
  }

  public PropertyFileDefaultProvider(final String fileName, final Function<String, String> optionNameTransformer) {
      init(fileName);
      this.optionNameTransformer = optionNameTransformer;
  }

  public PropertyFileDefaultProvider(final Path path) {
    this(path, DEFAULT_OPTION_NAME_TRANSFORMER);
  }

  public PropertyFileDefaultProvider(final Path path, final Function<String, String> optionNameTransformer) {
    try (final var inputStream = Files.newInputStream(path)) {
      properties.load(inputStream);
    } catch (final IOException e) {
      throw new ParameterException("Could not load properties from path: " + path);
    }
    this.optionNameTransformer = optionNameTransformer;
  }

  private void init(String fileName) {
    try {
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
  
  public String getDefaultValueFor(String optionName) {
    return properties.getProperty(optionName.transform(optionNameTransformer));
  }

}
