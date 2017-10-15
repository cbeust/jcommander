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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * An abstract formatter for help messages.
 */
public abstract class UsageFormatter {

    private final JCommander commander;

    public UsageFormatter(JCommander commander) {
        this.commander = commander;
    }

    /**
     * Display the usage for this command.
     */
    public final void usage(String commandName) {
        StringBuilder sb = new StringBuilder();
        usage(commandName, sb);
        commander.getConsole().println(sb.toString());
    }

    /**
     * Store the help for the command in the passed string builder.
     */
    public final void usage(String commandName, StringBuilder out) {
        usage(commandName, out, "");
    }

    /**
     * Store the help in the passed string builder.
     */
    public final void usage(StringBuilder out) {
        usage(out, "");
    }

    /**
     * Store the help for the command in the passed string builder, indenting every line with "indent".
     */
    public final void usage(String commandName, StringBuilder out, String indent) {
        String description = getCommandDescription(commandName);
        JCommander jc = commander.findCommandByAlias(commandName);

        if (description != null) {
            out.append(indent).append(description);
            out.append("\n");
        }
        jc.getUsageFormatter().usage(out, indent);
    }

    /**
     * Stores the help in the passed string builder, with the argument indentation.
     */
    public abstract void usage(StringBuilder out, String indent);

    /**
     * @return the description of the command.
     */
    public final String getCommandDescription(String commandName) {
        JCommander jc = commander.findCommandByAlias(commandName);

        if (jc == null) {
            throw new ParameterException("Asking description for unknown command: " + commandName);
        }
        Object arg = jc.getObjects().get(0);
        Parameters p = arg.getClass().getAnnotation(Parameters.class);
        java.util.ResourceBundle bundle;
        String result = null;

        if (p != null) {
            result = p.commandDescription();
            String bundleName = p.resourceBundle();

            if (!bundleName.isEmpty()) {
                bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
            } else {
                bundle = commander.getBundle();
            }

            if (bundle != null) {
                String descriptionKey = p.commandDescriptionKey();

                if (!descriptionKey.isEmpty()) {
                    result = getI18nString(bundle, descriptionKey, p.commandDescription());
                }
            }
        }
        return result;
    }

    /**
     * Wrap a potentially long line to {@link #commander#getColumnSize()}.
     *
     * @param out               the output
     * @param indent            the indentation in spaces for lines after the first line.
     * @param currentLineIndent the length of the indentation of the initial line
     * @param description       the text to wrap. No extra spaces are inserted before {@code
     *                          description}. If the first line needs to be indented prepend the
     *                          correct number of spaces to {@code description}.
     */
    protected void wrapDescription(StringBuilder out, int indent, int currentLineIndent, String description) {
        int max = getCommander().getColumnSize();
        String[] words = description.split(" ");
        int current = currentLineIndent;

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (word.length() > max || current + 1 + word.length() <= max) {
                out.append(word);
                current += word.length();

                if (i != words.length - 1) {
                    out.append(" ");
                    current++;
                }
            } else {
                out.append("\n").append(s(indent)).append(word).append(" ");
                current = indent + word.length() + 1;
            }
        }
    }

    /**
     * Wrap a potentially long line to {@link #commander#getColumnSize()}.
     *
     * @param out         the output
     * @param indent      the indentation in spaces for lines after the first line.
     * @param description the text to wrap. No extra spaces are inserted before {@code
     *                    description}. If the first line needs to be indented prepend the
     *                    correct number of spaces to {@code description}.
     * @see #wrapDescription(StringBuilder, int, int, String)
     */
    protected void wrapDescription(StringBuilder out, int indent, String description) {
        wrapDescription(out, indent, 0, description);
    }

    public JCommander getCommander() {
        return commander;
    }

    /**
     * @return The internationalized version of the string if available, otherwise
     * return def.
     */
    public static String getI18nString(ResourceBundle bundle, String key, String def) {
        String s = bundle != null ? bundle.getString(key) : null;
        return s != null ? s : def;
    }

    /**
     * @return <tt>count</tt>-many spaces
     */
    public static String s(int count) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < count; i++) {
            result.append(" ");
        }
        return result.toString();
    }
}
