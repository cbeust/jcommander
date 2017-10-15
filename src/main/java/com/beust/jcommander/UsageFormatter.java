package com.beust.jcommander;

import com.beust.jcommander.internal.Lists;

import java.util.*;
import java.util.ResourceBundle;

public class UsageFormatter {

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
    public void usage(StringBuilder out, String indent) {
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
                bundle = commander.getOptions().getBundle();
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
     * @param out         the output
     * @param indent      the indentation in spaces for lines after the first line.
     * @param description the text to wrap. No extra spaces are inserted before {@code
     *                    description}. If the first line needs to be indented prepend the
     *                    correct number of spaces to {@code description}.
     */
    private void wrapDescription(StringBuilder out, int indent, String description) {
        int max = commander.getColumnSize();
        String[] words = description.split(" ");
        int current = 0;
        int i = 0;

        while (i < words.length) {
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
                current = indent + 1 + word.length();
            }
            i++;
        }
    }

    /**
     * @return The internationalized version of the string if available, otherwise
     * return def.
     */
    private static String getI18nString(ResourceBundle bundle, String key, String def) {
        String s = bundle != null ? bundle.getString(key) : null;
        return s != null ? s : def;
    }

    /**
     * @return <tt>count</tt>-many spaces
     */
    private static String s(int count) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < count; i++) {
            result.append(" ");
        }
        return result.toString();
    }
}
