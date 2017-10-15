package com.beust.jcommander;

import com.beust.jcommander.internal.Lists;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class DefaultUsageFormatter extends UsageFormatter {

    public DefaultUsageFormatter(JCommander commander) {
        super(commander);
    }

    /**
     * Stores the help in the passed string builder, with the argument indentation.
     */
    public void usage(StringBuilder out, String indent) {
        JCommander commander = getCommander();

        if (commander.getDescriptions() == null) commander.createDescriptions();
        boolean hasCommands = !commander.getCommands().isEmpty();
        boolean hasOptions = !commander.getDescriptions().isEmpty();

        // Indentation constants
        final int descriptionIndent = 6;
        final int indentCount = indent.length() + descriptionIndent;

        //
        // First line of the usage
        //
        String programName = commander.getProgramDisplayName() != null
                ? commander.getProgramDisplayName() : "<main class>";
        StringBuilder mainLine = new StringBuilder();
        mainLine.append(indent).append("Usage: ").append(programName);
        if (hasOptions) mainLine.append(" [options]");
        if (hasCommands) mainLine.append(indent).append(" [command] [command options]");
        if (commander.getMainParameter() != null && commander.getMainParameter().getDescription() != null) {
            mainLine.append(" ").append(commander.getMainParameter().getDescription().getDescription());
        }
        wrapDescription(out, indentCount, mainLine.toString());
        out.append("\n");

        //
        // Align the descriptions at the "longestName" column
        //
        int longestName = 0;
        List<ParameterDescription> sorted = Lists.newArrayList();

        for (ParameterDescription pd : commander.getFields().values()) {
            if (!pd.getParameter().hidden()) {
                sorted.add(pd);
                // + to have an extra space between the name and the description
                int length = pd.getNames().length() + 2;

                if (length > longestName) {
                    longestName = length;
                }
            }
        }

        //
        // Sort the options
        //
        Collections.sort(sorted, commander.getParameterDescriptionComparator());

        //
        // Display all the names and descriptions
        //
        if (sorted.size() > 0) out.append(indent).append("  Options:\n");

        for (ParameterDescription pd : sorted) {
            WrappedParameter parameter = pd.getParameter();
            out.append(indent).append("  "
                    + (parameter.required() ? "* " : "  ")
                    + pd.getNames()
                    + "\n");
            wrapDescription(out, indentCount, s(indentCount) + pd.getDescription());
            Object def = pd.getDefault();
            if (pd.isDynamicParameter()) {
                out.append("\n" + s(indentCount))
                        .append("Syntax: " + parameter.names()[0]
                                + "key" + parameter.getAssignment()
                                + "value");
            }
            if (def != null && !pd.isHelp()) {
                String displayedDef = Strings.isStringEmpty(def.toString())
                        ? "<empty string>"
                        : def.toString();
                out.append("\n" + s(indentCount))
                        .append("Default: " + (parameter.password() ? "********" : displayedDef));
            }
            Class<?> type = pd.getParameterized().getType();
            if (type.isEnum()) {
                out.append("\n" + s(indentCount))
                        .append("Possible Values: " + EnumSet.allOf((Class<? extends Enum>) type));
            }
            out.append("\n");
        }

        //
        // If commands were specified, show them as well
        //
        if (hasCommands) {
            out.append(indent + "  Commands:\n");

            // The magic value 3 is the number of spaces between the name of the option
            // and its description
            for (Map.Entry<JCommander.ProgramName, JCommander> commands : commander.getCommands2().entrySet()) {
                Object arg = commands.getValue().getObjects().get(0);
                Parameters p = arg.getClass().getAnnotation(Parameters.class);

                if (p == null || !p.hidden()) {
                    JCommander.ProgramName progName = commands.getKey();
                    String dispName = progName.getDisplayName();
                    String description = getCommandDescription(progName.getName());
                    wrapDescription(out, indentCount + descriptionIndent,
                            indent + "    " + dispName + "      " + description);
                    out.append("\n");

                    // Options for this command
                    JCommander jc = commander.findCommandByAlias(progName.getName());
                    jc.getUsageFormatter().usage(out, indent + "      ");
                    out.append("\n");
                }
            }
        }
    }
}
