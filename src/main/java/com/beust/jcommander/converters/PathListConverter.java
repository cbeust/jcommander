/**
 * Copyright (C) 2021 the original author or authors.
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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.converters.BaseConverter;
import com.beust.jcommander.converters.PathConverter;

/**
 * Converts a string that may be a sequence of paths delimited by a comma
 * ({@code ,}) and optional whitespace into a {@link List} of {@link Path
 * paths}.
 *
 * @author twwwt
 */
public class PathListConverter extends BaseConverter<List<Path>>
{
  private final PathConverter pathConverter;

  public PathListConverter(final String optionName) {
    super(optionName);
    pathConverter = new PathConverter(optionName);
  }

  /* @see com.beust.jcommander.IStringConverter#convert(java.lang.String) */
  @Override
  public List<Path> convert(final String value) {
    String [] paths = value.split(",");
    List<Path> pathList = new ArrayList<>(paths.length);
    for (String path : paths) {
      pathList.add(pathConverter.convert(path.trim()));
    }
    return pathList;
  }
}
