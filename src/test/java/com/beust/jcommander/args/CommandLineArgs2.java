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

import java.io.File;
import java.util.List;

public class CommandLineArgs2 {
  @Parameter(description = "list of files")
  List<String> list;

  @Parameter(names = { "-v", "--verbose" }, description = "print verbose log messages.", arity = 1)
  public boolean verbose = false;

  @Parameter(names = { "-h", "--help" }, description = "show this help.")
  public boolean showHelp = false;

  @Parameter(names = { "-F", "--flush-preferences" }, description = "flush gui preferences.")
  public boolean flushPreferences = false;

  @Parameter(names = { "-L", "--flush-licensed" }, description = "flush licensed.")
  public boolean flushLicensed = false;

  @Parameter(names = { "-I", "--index-file" }, description = "indexes the given file.")
  public Long indexFile;

  @Parameter(names = { "-b", "--bonjour" }, description = "enable Bonjour.")
  public boolean enableBonjour = false;

  @Parameter(names = { "-m", "--md5" }, description = "create an MD5 checksum for the given file.", converter = FileConverter.class)
  public File md5File;

  @Parameter(names = { "-c", "--cat" }, description = "'cat' the given Lilith logfile.", converter = FileConverter.class)
  public File catFile;

  @Parameter(names = { "-t", "--tail" }, description = "'tail' the given Lilith logfile.", converter = FileConverter.class)
  public File tailFile;

  @Parameter(names = { "-p", "--pattern" }, description = "pattern used by 'cat' or 'tail'.")
  public String pattern;

  @Parameter(names = { "-f", "--keep-running" }, description = "keep tailing the given Lilith logfile.")
  public boolean keepRunning = false;

  @Parameter(names = { "-n", "--number-of-lines" }, description = "number of entries printed by cat or tail")
  public Integer numberOfLines = -1;

  @Parameter(names = { "-e", "--export-preferences" }, description = "export preferences into the given file.")
  public String exportPreferencesFile;

  @Parameter(names = { "-i", "--import-preferences" }, description = "import preferences from the given file.")
  public String importPreferencesFile;
}
