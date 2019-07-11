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

import com.beust.jcommander.internal.Lists;

import java.util.*;
import java.util.ResourceBundle;

/**
 * The default usage formatter.
 */
public class DefaultUsageFormatter implements IUsageFormatter {
    public DefaultUsageFormatter() {
    }

    /**
     * Returns the internationalized version of the string if available, otherwise it returns <tt>def</tt>.
     * @return the internationalized version of the string if available, otherwise it returns <tt>def</tt>
     */
    protected static String getI18nString(ResourceBundle bundle, String key, String def) {
        String s = bundle != null ? bundle.getString(key) : null;
        return s != null ? s : def;
    }

    /**
     * Returns <tt>count</tt>-many spaces.
     * @return <tt>count</tt>-many spaces
     */
    protected static String s(int count) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < count; i++) {
            result.append(" ");
        }
        return result.toString();
    }

    /**
     * Returns new line followed by <tt>indent</tt>-many spaces.
     * @return new line followed by <tt>indent</tt>-many spaces
     */
    private static String newLineAndIndent(int indent) {
        return "\n" + s(indent);
    }

    /**
     * Prints the usage to {@link JCommander#getConsole()} on the underlying commander instance.
     */
    @Override
    public final void usage(JCommander commander, String commandName) {
        StringBuilder sb = new StringBuilder();
        usage(commander, commandName, sb);
        commander.getConsole().println(sb.toString());
    }

    /**
     * Store the usage for the argument command in the argument string builder.
     */
    @Override
    public final void usage(JCommander commander, String commandName, StringBuilder out) {
        usage(commander, commandName, out, "");
    }

    /**
     * Store the usage in the argument string builder.
     */
    @Override
    public final void usage(JCommander commander, StringBuilder out) {
        usage(commander, out, "");
    }

    /**
     * Store the usage for the command in the argument string builder, indenting every line with the
     * value of <tt>indent</tt>.
     */
    @Override
    public final void usage(
        JCommander commander, String commandName, StringBuilder out, String indent) {
        String description = getCommandDescription(commander, commandName);
        JCommander jc = commander.findCommandByAlias(commandName);

        if (description != null) {
            out.append(indent).append(description);
            out.append("\n");
        }
        usage(jc, out, indent);
    }

    /**
     * Stores the usage in the argument string builder, with the argument indentation. This works by appending
     * each portion of the help in the following order. Their outputs can be modified by overriding them in a
     * subclass of this class.
     *
     * <ul>
     * <li>Main line - {@link #appendMainLine(JCommander, StringBuilder, boolean, boolean, int, String)}</li>
     * <li>Parameters - {@link #appendAllParametersDetails(JCommander, StringBuilder, int, String, List)}</li>
     * <li>Commands - {@link #appendCommands(JCommander, StringBuilder, int, int, String)}</li>
     * </ul>
     */
    @Override
    public void usage(JCommander commander, StringBuilder out, String indent) {
        if (commander.getDescriptions() == null) {
            commander.createDescriptions();
        }
        boolean hasCommands = !commander.getCommands().isEmpty();
        boolean hasOptions = !commander.getDescriptions().isEmpty();

        // Indentation constants
        final int descriptionIndent = 6;
        final int indentCount = indent.length() + descriptionIndent;

        // Append first line (aka main line) of the usage
        appendMainLine(commander, out, hasOptions, hasCommands, indentCount, indent);

        // Align the descriptions at the "longestName" column
        int longestName = 0;
        List<ParameterDescription> sortedParameters = Lists.newArrayList();

        for (ParameterDescription pd : commander.getFields().values()) {
            if (!pd.getParameter().hidden()) {
                sortedParameters.add(pd);
                // + to have an extra space between the name and the description
                int length = pd.getNames().length() + 2;

                if (length > longestName) {
                    longestName = length;
                }
            }
        }

        // Sort the options
        sortedParameters.sort(commander.getParameterDescriptionComparator());

        // Append all the parameter names and descriptions
        appendAllParametersDetails(commander, out, indentCount, indent, sortedParameters);

        // Append commands if they were specified
        if (hasCommands) {
            appendCommands(commander, out, indentCount, descriptionIndent, indent);
        }
    }

    /**
     * Appends the main line segment of the usage to the argument string builder, indenting every
     * line with <tt>indentCount</tt>-many <tt>indent</tt>.
     * @param commander the commander
     * @param out the builder to append to
     * @param hasOptions if the options section should be appended
     * @param hasCommands if the comments section should be appended
     * @param indentCount the amount of indentation to apply
     * @param indent the indentation
     */
    protected void appendMainLine(
        JCommander commander, StringBuilder out, boolean hasOptions, boolean hasCommands,
        int indentCount,
        String indent) {
        String programName = commander.getProgramDisplayName() != null
            ? commander.getProgramDisplayName() : "<main class>";
        StringBuilder mainLine = new StringBuilder();
        mainLine.append(indent).append("Usage: ").append(programName);

        if (hasOptions) {
            mainLine.append(" [options]");
        }

        if (hasCommands) {
            mainLine.append(indent).append(" [command] [command options]");
        }

        if (commander.getMainParameter() != null
            && commander.getMainParameter().getDescription() != null) {
            mainLine.append(" ")
                .append(commander.getMainParameter().getDescription().getDescription());
        }
        wrapDescription(commander, out, indentCount, mainLine.toString());
        out.append("\n");
    }

    /**
     * Appends the details of all parameters in the given order to the argument string builder, indenting every
     * line with <tt>indentCount</tt>-many <tt>indent</tt>.
     * @param commander the commander
     * @param out the builder to append to
     * @param indentCount the amount of indentation to apply
     * @param indent the indentation
     * @param sortedParameters the parameters to append to the builder
     */
    protected void appendAllParametersDetails(
        JCommander commander, StringBuilder out, int indentCount, String indent,
        List<ParameterDescription> sortedParameters) {
        if (sortedParameters.size() > 0) {
            out.append(indent).append("  Options:\n");
        }

        for (ParameterDescription pd : sortedParameters) {
            WrappedParameter parameter = pd.getParameter();
            String description = pd.getDescription();
            boolean hasDescription = !description.isEmpty();

            // First line, command name
            out.append(indent)
                .append("  ")
                .append(parameter.required() ? "* " : "  ")
                .append(pd.getNames())
                .append("\n");

            if (hasDescription) {
                wrapDescription(commander, out, indentCount, s(indentCount) + description);
            }
            Object def = pd.getDefault();

            if (pd.isDynamicParameter()) {
                String syntax =
                    "Syntax: " + parameter.names()[0] + "key" + parameter.getAssignment() + "value";

                if (hasDescription) {
                    out.append(newLineAndIndent(indentCount));
                } else {
                    out.append(s(indentCount));
                }
                out.append(syntax);
            }

            if (def != null && !pd.isHelp()) {
                String displayedDef =
                    Strings.isStringEmpty(def.toString()) ? "<empty string>" : def.toString();
                String defaultText = "Default: " + (parameter.password() ? "********" : displayedDef);

                if (hasDescription) {
                    out.append(newLineAndIndent(indentCount));
                } else {
                    out.append(s(indentCount));
                }
                out.append(defaultText);
            }
            Class<?> type = pd.getParameterized().getType();

            if (type.isEnum()) {
                String valueList = EnumSet.allOf((Class<? extends Enum>) type).toString();
                String possibleValues = "Possible Values: " + valueList;

                // Prevent duplicate values list, since it is set as 'Options: [values]' if the description
                // of an enum field is empty in ParameterDescription#init(..)
                if (!description.contains("Options: " + valueList)) {
                    if (hasDescription) {
                        out.append(newLineAndIndent(indentCount));
                    } else {
                        out.append(s(indentCount));
                    }
                    out.append(possibleValues);
                }
            }
            out.append("\n");
        }
    }

    /**
     * Appends the details of all commands to the argument string builder, indenting every line with
     * <tt>indentCount</tt>-many <tt>indent</tt>. The commands are obtained from calling
     * {@link JCommander#getRawCommands()} and the commands are resolved using
     * {@link JCommander#findCommandByAlias(String)} on the underlying commander instance.
     * @param commander the commander
     * @param out the builder to append to
     * @param indentCount the amount of indentation to apply
     * @param descriptionIndent the indentation for the description
     * @param indent the indentation
     */
    protected void appendCommands(
        JCommander commander, StringBuilder out, int indentCount, int descriptionIndent,
        String indent) {
        out.append(indent + "  Commands:\n");

        // The magic value 3 is the number of spaces between the name of the option and its description
        for (Map.Entry<JCommander.ProgramName, JCommander> commands : commander.getRawCommands()
            .entrySet()) {
            Object arg = commands.getValue().getObjects().get(0);
            Parameters p = arg.getClass().getAnnotation(Parameters.class);

            if (p == null || !p.hidden()) {
                JCommander.ProgramName progName = commands.getKey();
                String dispName = progName.getDisplayName();
                String description = indent + s(4) + dispName + s(6) + getCommandDescription(commander,
                    progName.getName());
                wrapDescription(commander, out, indentCount + descriptionIndent, description);
                out.append("\n");

                // Options for this command
                JCommander jc = commander.findCommandByAlias(progName.getName());
                usage(jc, out, indent + s(6));
                out.append("\n");
            }
        }
    }

    /**
     * Returns the description of the command corresponding to the argument command name. The commands are resolved
     * by calling {@link JCommander#findCommandByAlias(String)}, and the default resource bundle used from
     * {@link JCommander#getBundle()} on the underlying commander instance.
     * @param commander the commander
     * @param commandName the name of the command to get the description for
     * @return the description of the command.
     */
    @Override
    public String getCommandDescription(JCommander commander, String commandName) {
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
     * Wrap a potentially long line to the value obtained by calling {@link JCommander#getColumnSize()} on the
     * underlying commander instance.
     * @param commander the commander
     * @param out the output
     * @param indent the indentation in spaces for lines after the first line.
     * @param currentLineIndent the length of the indentation of the initial line
     * @param description the text to wrap. No extra spaces are inserted before {@code
     * description}. If the first line needs to be indented prepend the
     * correct number of spaces to {@code description}.
     */
    protected void wrapDescription(
        JCommander commander, StringBuilder out, int indent, int currentLineIndent,
        String description) {
        int max = commander.getColumnSize();
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
     * Wrap a potentially long line to {@link JCommander#getColumnSize()}.
     * @param commander the commander
     * @param out the output
     * @param indent the indentation in spaces for lines after the first line.
     * @param description the text to wrap. No extra spaces are inserted before {@code
     * description}. If the first line needs to be indented prepend the
     * correct number of spaces to {@code description}.
     * @see #wrapDescription(JCommander, StringBuilder, int, int, String)
     */
    protected void wrapDescription(
        JCommander commander, StringBuilder out, int indent, String description) {
        wrapDescription(commander, out, indent, 0, description);
    }
}
