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

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * The default usage formatter.
 */
public class DefaultUsageFormatter extends UsageFormatter {

    public DefaultUsageFormatter(JCommander commander) {
        super(commander);
    }

    /**
     * Stores the help in the passed string builder, with the argument indentation.
     */
    public void usage(StringBuilder out, String indent) {
        if (getCommander().getDescriptions() == null)
            getCommander().createDescriptions();
        boolean hasCommands = !getCommander().getCommands().isEmpty();
        boolean hasOptions = !getCommander().getDescriptions().isEmpty();

        // Indentation constants
        final int descriptionIndent = 6;
        final int indentCount = indent.length() + descriptionIndent;

        // Append first line (aka main line) of the usage
        appendMainLine(out, hasOptions, hasCommands, indentCount, indent);

        // Align the descriptions at the "longestName" column
        int longestName = 0;
        List<ParameterDescription> sortedParameters = Lists.newArrayList();

        for (ParameterDescription pd : getCommander().getFields().values()) {
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
        sortedParameters.sort(getCommander().getParameterDescriptionComparator());

        // Append all the parameter names and descriptions
        appendAllParametersDetails(out, indentCount, indent, sortedParameters);

        // Append commands if they were specified
        if (hasCommands) {
            appendCommands(out, indentCount, descriptionIndent, indent);
        }
    }

    protected void appendMainLine(StringBuilder out, boolean hasOptions, boolean hasCommands, int indentCount,
            String indent) {
        String programName = getCommander().getProgramDisplayName() != null
                ? getCommander().getProgramDisplayName() : "<main class>";
        StringBuilder mainLine = new StringBuilder();
        mainLine.append(indent).append("Usage: ").append(programName);

        if (hasOptions)
            mainLine.append(" [options]");
        if (hasCommands)
            mainLine.append(indent).append(" [command] [command options]");
        if (getCommander().getMainParameter() != null && getCommander().getMainParameter().getDescription() != null)
            mainLine.append(" ").append(getCommander().getMainParameter().getDescription().getDescription());
        wrapDescription(out, indentCount, mainLine.toString());
        out.append("\n");
    }

    protected void appendAllParametersDetails(StringBuilder out, int indentCount, String indent,
            List<ParameterDescription> sortedParameters) {
        if (sortedParameters.size() > 0)
            out.append(indent).append("  Options:\n");

        for (ParameterDescription pd : sortedParameters) {
            WrappedParameter parameter = pd.getParameter();

            // First line, command name
            out.append(indent)
                    .append("  ")
                    .append(parameter.required() ? "* " : "  ")
                    .append(pd.getNames())
                    .append("\n");
            wrapDescription(out, indentCount, s(indentCount) + pd.getDescription());
            Object def = pd.getDefault();

            if (pd.isDynamicParameter()) {
                String syntax = "Syntax: " + parameter.names()[0] + "key" + parameter.getAssignment() + "value";
                out.append(newLineAndIndent(indentCount)).append(syntax);
            }

            if (def != null && !pd.isHelp()) {
                String displayedDef = Strings.isStringEmpty(def.toString()) ? "<empty string>" : def.toString();
                String defaultText = "Default: " + (parameter.password() ? "********" : displayedDef);
                out.append(newLineAndIndent(indentCount)).append(defaultText);
            }
            Class<?> type = pd.getParameterized().getType();

            if (type.isEnum()) {
                String possibleValues = "Possible Values: " + EnumSet.allOf((Class<? extends Enum>) type);
                out.append(newLineAndIndent(indentCount)).append(possibleValues);
            }
            out.append("\n");
        }
    }

    protected void appendCommands(StringBuilder out, int indentCount, int descriptionIndent, String indent) {
        out.append(indent + "  Commands:\n");

        // The magic value 3 is the number of spaces between the name of the option and its description
        for (Map.Entry<JCommander.ProgramName, JCommander> commands : getCommander().getRawCommands().entrySet()) {
            Object arg = commands.getValue().getObjects().get(0);
            Parameters p = arg.getClass().getAnnotation(Parameters.class);

            if (p == null || !p.hidden()) {
                JCommander.ProgramName progName = commands.getKey();
                String dispName = progName.getDisplayName();
                String description = indent + s(4) + dispName + s(6) + getCommandDescription(progName.getName());
                wrapDescription(out, indentCount + descriptionIndent, description);
                out.append("\n");

                // Options for this command
                JCommander jc = getCommander().findCommandByAlias(progName.getName());
                jc.getUsageFormatter().usage(out, indent + s(6));
                out.append("\n");
            }
        }
    }

    private static String newLineAndIndent(int indent) {
        return "\n" + s(indent);
    }
}
