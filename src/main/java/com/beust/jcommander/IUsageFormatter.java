/**
 * Copyright (C) 2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beust.jcommander;

/**
 * A formatter for help messages.
 */
public interface IUsageFormatter {

    /**
     * Display the usage for this command.
     */
    void usage(JCommander commander, String commandName);

    /**
     * Store the help for the command in the passed string builder.
     */
    void usage(JCommander commander, String commandName, StringBuilder out);

    /**
     * Store the help in the passed string builder.
     */
    void usage(JCommander commander, StringBuilder out);

    /**
     * Store the help for the command in the passed string builder, indenting every line with "indent".
     */
    void usage(JCommander commander, String commandName, StringBuilder out, String indent);

    /**
     * Stores the help in the passed string builder, with the argument indentation.
     */
    void usage(JCommander commander, StringBuilder out, String indent);

    /**
     * @return the description of the argument command
     */
    String getCommandDescription(JCommander commander, String commandName);
}
