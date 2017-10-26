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

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Convert a string into a path.
 *
 * @author samvv
 */
public class PathConverter extends BaseConverter<Path> {

  public PathConverter(String optionName) {
    super(optionName);
  }

  public Path convert(String value) {
    try {
      return Paths.get(value);
    } catch (InvalidPathException e) {
      String encoded = escapeUnprintable(value);
      throw new ParameterException(getErrorString(encoded, "a path"));
    }
  }

  private static String escapeUnprintable(String value) {
    StringBuilder bldr = new StringBuilder();
    for (char c: value.toCharArray()) {
        if (c < ' ') {
            bldr.append("\\u").append(String.format("%04X", (int) c));
        } else {
            bldr.append(c);
        }
    }
    return bldr.toString();
  }
}
