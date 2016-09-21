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

/**
 * Thrown when a command was expected.
 *
 * @author Cedric Beust <cedric@beust.com>
 */
@SuppressWarnings("serial")
public class MissingCommandException extends ParameterException {

  /**
   * the command passed by the user.
   */
  private final String unknownCommand;

  public MissingCommandException(String message) {
    this(message, null);
  }

  public MissingCommandException(String message, String command) {
    super(message);
    this.unknownCommand = command;
  }

  public String getUnknownCommand() {
    return unknownCommand;
  }

}
