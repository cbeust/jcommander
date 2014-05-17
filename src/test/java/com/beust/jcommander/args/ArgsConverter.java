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
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.PathConverter;
import com.beust.jcommander.converters.URIConverter;
import com.beust.jcommander.converters.URLConverter;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class ArgsConverter {

  @Parameter(names = "-file", converter = FileConverter.class)
  public File file;
  
  @Parameter(names = "-url", converter = URLConverter.class)
  public URL url;
  
  @Parameter(names = "-uri", converter = URIConverter.class)
  public URI uri;
  
  @Parameter(names = "-path", converter = PathConverter.class)
  public Path path;
  
  @Parameter(names = "-listStrings")
  public List<String> listStrings;

  @Parameter(names = "-listInts")
  public List<Integer> listInts;

  @Parameter(names = "-listBigDecimals")
  public List<BigDecimal> listBigDecimals;
  
}
