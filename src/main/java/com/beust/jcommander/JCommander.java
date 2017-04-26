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

import com.beust.jcommander.FuzzyMap.IKey;
import com.beust.jcommander.converters.*;
import com.beust.jcommander.internal.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The main class for JCommander. It's responsible for parsing the object that contains
 * all the annotated fields, parse the command line and assign the fields with the correct
 * values and a few other helper methods, such as usage().
 *
 * The object(s) you pass in the constructor are expected to have one or more
 * \@Parameter annotations on them. You can pass either a single object, an array of objects
 * or an instance of Iterable. In the case of an array or Iterable, JCommander will collect
 * the \@Parameter annotations from all the objects passed in parameter.
 *
 * @author Cedric Beust <cedric@beust.com>
 */
public class JCommander {
    public static final String DEBUG_PROPERTY = "jcommander.debug";

    /**
     * A map to look up parameter description per option name.
     */
    private Map<IKey, ParameterDescription> descriptions;

    /**
     * The objects that contain fields annotated with @Parameter.
     */
    private List<Object> objects = Lists.newArrayList();

    private boolean firstTimeMainParameter = true;

    /**
     * This field/method will contain whatever command line parameter is not an option.
     * It is expected to be a List<String>.
     */
    private Parameterized mainParameter = null;

    /**
     * The object on which we found the main parameter field.
     */
    private Object mainParameterObject;

    /**
     * The annotation found on the main parameter field.
     */
    private Parameter mainParameterAnnotation;

    private ParameterDescription mainParameterDescription;

    /**
     * A set of all the parameterizeds that are required. During the reflection phase,
     * this field receives all the fields that are annotated with required=true
     * and during the parsing phase, all the fields that are assigned a value
     * are removed from it. At the end of the parsing phase, if it's not empty,
     * then some required fields did not receive a value and an exception is
     * thrown.
     */
    private Map<Parameterized, ParameterDescription> requiredFields = Maps.newHashMap();

    /**
     * A map of all the parameterized fields/methods.
     */
    private Map<Parameterized, ParameterDescription> fields = Maps.newHashMap();

    /**
     * List of commands and their instance.
     */
    private Map<ProgramName, JCommander> commands = Maps.newLinkedHashMap();

    /**
     * Alias database for reverse lookup
     */
    private Map<IKey, ProgramName> aliasMap = Maps.newLinkedHashMap();

    /**
     * The name of the command after the parsing has run.
     */
    private String parsedCommand;

    /**
     * The name of command or alias as it was passed to the
     * command line
     */
    private String parsedAlias;

    private ProgramName programName;

    private boolean helpWasSpecified;

    private List<String> unknownArgs = Lists.newArrayList();

    private static Console console;

    private final Options options;

    /**
     * Options shared with sub commands
     */
    private static class Options {

        private ResourceBundle bundle;

        /**
         * A default provider returns default values for the parameters.
         */
        private IDefaultProvider defaultProvider;

        private Comparator<? super ParameterDescription> parameterDescriptionComparator
                = new Comparator<ParameterDescription>() {
            @Override
            public int compare(ParameterDescription p0, ParameterDescription p1) {
                Parameter a0 = p0.getParameterAnnotation();
                Parameter a1 = p1.getParameterAnnotation();
                if (a0 != null && a0.order() != -1 && a1 != null && a1.order() != -1) {
                    return Integer.compare(a0.order(), a1.order());
                } else if (a0 != null && a0.order() != -1) {
                    return -1;
                } else if (a1 != null && a1.order() != -1) {
                    return 1;
                } else {
                    return p0.getLongestName().compareTo(p1.getLongestName());
                }
            }
        };
        private int columnSize = 79;
        private boolean acceptUnknownOptions = false;
        private boolean allowParameterOverwriting = false;
        private boolean expandAtSign = true;
        private int verbose = 0;
        private boolean caseSensitiveOptions = true;
        private boolean allowAbbreviatedOptions = false;
        /**
         * The factories used to look up string converters.
         */
        private final List<IStringConverterInstanceFactory> converterInstanceFactories = new CopyOnWriteArrayList<>();
        private Charset atFileCharset = Charset.defaultCharset();
    }

    private JCommander(Options options) {
        if (options == null) {
            throw new NullPointerException("options");
        }
        this.options = options;
        addConverterFactory(new DefaultConverterFactory());
    }

    /**
     * Creates a new un-configured JCommander object.
     */
    public JCommander() {
        this(new Options());
    }

    /**
     * @param object The arg object expected to contain {@link Parameter} annotations.
     */
    public JCommander(Object object) {
        this(object, (ResourceBundle) null);
    }

    /**
     * @param object The arg object expected to contain {@link Parameter} annotations.
     * @param bundle The bundle to use for the descriptions. Can be null.
     */
    public JCommander(Object object, @Nullable ResourceBundle bundle) {
        this(object, bundle, (String[]) null);
    }

    /**
     * @param object The arg object expected to contain {@link Parameter} annotations.
     * @param bundle The bundle to use for the descriptions. Can be null.
     * @param args The arguments to parse (optional).
     */
    public JCommander(Object object, @Nullable  ResourceBundle bundle, String... args) {
        this();
        addObject(object);
        if (bundle != null) {
            setDescriptionsBundle(bundle);
        }
        createDescriptions();
        if (args != null) {
            parse(args);
        }
    }

    /**
     * @param object The arg object expected to contain {@link Parameter} annotations.
     * @param args The arguments to parse (optional).
     *
     * @deprecated Construct a JCommander instance first and then call parse() on it.
     */
    @Deprecated()
    public JCommander(Object object, String... args) {
        this(object);
        parse(args);
    }

    /**
     * Disables expanding {@code @file}.
     *
     * JCommander supports the {@code @file} syntax, which allows you to put all your options
     * into a file and pass this file as parameter @param expandAtSign whether to expand {@code @file}.
     */
    public void setExpandAtSign(boolean expandAtSign) {
        options.expandAtSign = expandAtSign;
    }

    public static Console getConsole() {
        if (console == null) {
            try {
                Method consoleMethod = System.class.getDeclaredMethod("console");
                Object console = consoleMethod.invoke(null);
                JCommander.console = new JDK6Console(console);
            } catch (Throwable t) {
                console = new DefaultConsole();
            }
        }
        return console;
    }

    /**
     * Adds the provided arg object to the set of objects that this commander
     * will parse arguments into.
     *
     * @param object The arg object expected to contain {@link Parameter}
     * annotations. If <code>object</code> is an array or is {@link Iterable},
     * the child objects will be added instead.
     */
    // declared final since this is invoked from constructors
    public final void addObject(Object object) {
        if (object instanceof Iterable) {
            // Iterable
            for (Object o : (Iterable<?>) object) {
                objects.add(o);
            }
        } else if (object.getClass().isArray()) {
            // Array
            for (Object o : (Object[]) object) {
                objects.add(o);
            }
        } else {
            // Single object
            objects.add(object);
        }
    }

    /**
     * Sets the {@link ResourceBundle} to use for looking up descriptions.
     * Set this to <code>null</code> to use description text directly.
     */
    // declared final since this is invoked from constructors
    public final void setDescriptionsBundle(ResourceBundle bundle) {
        options.bundle = bundle;
    }

    /**
     * Parse and validate the command line parameters.
     */
    public void parse(String... args) {
        try {
            parse(true /* validate */, args);
        } catch(ParameterException ex) {
            ex.setJCommander(this);
            throw ex;
        }
    }

    /**
     * Parse the command line parameters without validating them.
     */
    public void parseWithoutValidation(String... args) {
        parse(false /* no validation */, args);
    }

    private void parse(boolean validate, String... args) {
        StringBuilder sb = new StringBuilder("Parsing \"");
        sb.append(join(args).append("\"\n  with:").append(join(objects.toArray())));
        p(sb.toString());

        if (descriptions == null) createDescriptions();
        initializeDefaultValues();
        parseValues(expandArgs(args), validate);
        if (validate) validateOptions();
    }

    private StringBuilder join(Object[] args) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) result.append(" ");
            result.append(args[i]);
        }
        return result;
    }

    private void initializeDefaultValues() {
        if (options.defaultProvider != null) {
            for (ParameterDescription pd : descriptions.values()) {
                initializeDefaultValue(pd);
            }

            for (Map.Entry<ProgramName, JCommander> entry : commands.entrySet()) {
                entry.getValue().initializeDefaultValues();
            }
        }
    }

    /**
     * Make sure that all the required parameters have received a value.
     */
    private void validateOptions() {
        // No validation if we found a help parameter
        if (helpWasSpecified) {
            return;
        }

        if (!requiredFields.isEmpty()) {
            List<String> missingFields = new ArrayList<>();
            for (ParameterDescription pd : requiredFields.values()) {
                missingFields.add("[" + String.join(" | ", pd.getParameter().names()) + "]");
            }
            String message = String.join(", ", missingFields);
            throw new ParameterException("The following "
                    + pluralize(requiredFields.size(), "option is required: ", "options are required: ")
                    + message);
        }

        if (mainParameterDescription != null) {
            if (mainParameterDescription.getParameter().required() &&
                    !mainParameterDescription.isAssigned()) {
                throw new ParameterException("Main parameters are required (\""
                        + mainParameterDescription.getDescription() + "\")");
            }
        }
    }

    private static String pluralize(int quantity, String singular, String plural) {
        return quantity == 1 ? singular : plural;
    }

    /**
     * Expand the command line parameters to take @ parameters into account.
     * When @ is encountered, the content of the file that follows is inserted
     * in the command line.
     *
     * @param originalArgv the original command line parameters
     * @return the new and enriched command line parameters
     */
    private String[] expandArgs(String[] originalArgv) {
        List<String> vResult1 = Lists.newArrayList();

        //
        // Expand @
        //
        for (String arg : originalArgv) {

            if (arg.startsWith("@") && options.expandAtSign) {
                String fileName = arg.substring(1);
                vResult1.addAll(readFile(fileName));
            } else {
                List<String> expanded = expandDynamicArg(arg);
                vResult1.addAll(expanded);
            }
        }

        // Expand separators
        //
        List<String> vResult2 = Lists.newArrayList();
        for (String arg : vResult1) {
            if (isOption(arg)) {
                String sep = getSeparatorFor(arg);
                if (!" ".equals(sep)) {
                    String[] sp = arg.split("[" + sep + "]", 2);
                    for (String ssp : sp) {
                        vResult2.add(ssp);
                    }
                } else {
                    vResult2.add(arg);
                }
            } else {
                vResult2.add(arg);
            }
        }

        return vResult2.toArray(new String[vResult2.size()]);
    }

    private List<String> expandDynamicArg(String arg) {
        for (ParameterDescription pd : descriptions.values()) {
            if (pd.isDynamicParameter()) {
                for (String name : pd.getParameter().names()) {
                    if (arg.startsWith(name) && !arg.equals(name)) {
                        return Arrays.asList(name, arg.substring(name.length()));
                    }
                }
            }
        }

        return Arrays.asList(arg);
    }

    private boolean matchArg(String arg, IKey key) {
        String kn = options.caseSensitiveOptions
                ? key.getName()
                : key.getName().toLowerCase();
        if (options.allowAbbreviatedOptions) {
            if (kn.startsWith(arg)) return true;
        } else {
            ParameterDescription pd = descriptions.get(key);
            if (pd != null) {
                // It's an option. If the option has a separator (e.g. -author==foo) then
                // we only do a beginsWith match
                String separator = getSeparatorFor(arg);
                if (! " ".equals(separator)) {
                    if (arg.startsWith(kn)) return true;
                } else {
                    if (kn.equals(arg)) return true;
                }
            } else {
                // It's a command do a strict equality check
                if (kn.equals(arg)) return true;
            }
        }
        return false;
    }

    private boolean isOption(String passedArg) {
        if (options.acceptUnknownOptions) return true;

        String arg = options.caseSensitiveOptions ? passedArg : passedArg.toLowerCase();

        for (IKey key : descriptions.keySet()) {
            if (matchArg(arg, key)) return true;
        }
        for (IKey key : commands.keySet()) {
            if (matchArg(arg, key)) return true;
        }

        return false;
    }

    private ParameterDescription getPrefixDescriptionFor(String arg) {
        for (Map.Entry<IKey, ParameterDescription> es : descriptions.entrySet()) {
            if (arg.startsWith(es.getKey().getName())) return es.getValue();
        }

        return null;
    }

    /**
     * If arg is an option, we can look it up directly, but if it's a value,
     * we need to find the description for the option that precedes it.
     */
    private ParameterDescription getDescriptionFor(String arg) {
        return getPrefixDescriptionFor(arg);
    }

    private String getSeparatorFor(String arg) {
        ParameterDescription pd = getDescriptionFor(arg);

        // Could be null if only main parameters were passed
        if (pd != null) {
            Parameters p = pd.getObject().getClass().getAnnotation(Parameters.class);
            if (p != null) return p.separators();
        }

        return " ";
    }

    /**
     * Reads the file specified by filename and returns the file content as a string.
     * End of lines are replaced by a space.
     *
     * @param fileName the command line filename
     * @return the file content as a string.
     */
    private List<String> readFile(String fileName) {
        List<String> result = Lists.newArrayList();

        try (BufferedReader bufRead = Files.newBufferedReader(Paths.get(fileName), options.atFileCharset)) {
            String line;
            // Read through file one line at time. Print line # and line
            while ((line = bufRead.readLine()) != null) {
                // Allow empty lines and # comments in these at files
                if (line.length() > 0 && !line.trim().startsWith("#")) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            throw new ParameterException("Could not read file " + fileName + ": " + e);
        }

        return result;
    }

    /**
     * Remove spaces at both ends and handle double quotes.
     */
    private static String trim(String string) {
        String result = string.trim();
        if (result.startsWith("\"") && result.endsWith("\"") && result.length() > 1) {
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }

    /**
     * Create the ParameterDescriptions for all the \@Parameter found.
     */
    private void createDescriptions() {
        descriptions = Maps.newHashMap();

        for (Object object : objects) {
            addDescription(object);
        }
    }

    private void addDescription(Object object) {
        Class<?> cls = object.getClass();

        List<Parameterized> parameterizeds = Parameterized.parseArg(object);
        for (Parameterized parameterized : parameterizeds) {
            WrappedParameter wp = parameterized.getWrappedParameter();
            if (wp != null && wp.getParameter() != null) {
                Parameter annotation = wp.getParameter();
                //
                // @Parameter
                //
                Parameter p = annotation;
                if (p.names().length == 0) {
                    p("Found main parameter:" + parameterized);
                    if (mainParameter != null) {
                        throw new ParameterException("Only one @Parameter with no names attribute is"
                                + " allowed, found:" + mainParameter + " and " + parameterized);
                    }
                    mainParameter = parameterized;
                    mainParameterObject = object;
                    mainParameterAnnotation = p;
                    mainParameterDescription =
                            new ParameterDescription(object, p, parameterized, options.bundle, this);
                } else {
                    ParameterDescription pd =
                            new ParameterDescription(object, p, parameterized, options.bundle, this);
                    for (String name : p.names()) {
                        if (descriptions.containsKey(new StringKey(name))) {
                            throw new ParameterException("Found the option " + name + " multiple times");
                        }
                        p("Adding description for " + name);
                        fields.put(parameterized, pd);
                        descriptions.put(new StringKey(name), pd);

                        if (p.required()) requiredFields.put(parameterized, pd);
                    }
                }
            } else if (parameterized.getDelegateAnnotation() != null) {
                //
                // @ParametersDelegate
                //
                Object delegateObject = parameterized.get(object);
                if (delegateObject == null) {
                    throw new ParameterException("Delegate field '" + parameterized.getName()
                            + "' cannot be null.");
                }
                addDescription(delegateObject);
            } else if (wp != null && wp.getDynamicParameter() != null) {
                //
                // @DynamicParameter
                //
                DynamicParameter dp = wp.getDynamicParameter();
                for (String name : dp.names()) {
                    if (descriptions.containsKey(name)) {
                        throw new ParameterException("Found the option " + name + " multiple times");
                    }
                    p("Adding description for " + name);
                    ParameterDescription pd =
                            new ParameterDescription(object, dp, parameterized, options.bundle, this);
                    fields.put(parameterized, pd);
                    descriptions.put(new StringKey(name), pd);

                    if (dp.required()) requiredFields.put(parameterized, pd);
                }
            }
        }
    }

    private void initializeDefaultValue(ParameterDescription pd) {
        for (String optionName : pd.getParameter().names()) {
            String def = options.defaultProvider.getDefaultValueFor(optionName);
            if (def != null) {
                p("Initializing " + optionName + " with default value:" + def);
                pd.addValue(def, true /* default */);
                // remove the parameter from the list of fields to be required
                requiredFields.remove(pd.getParameterized());
                return;
            }
        }
    }

    /**
     * Main method that parses the values and initializes the fields accordingly.
     */
    private void parseValues(String[] args, boolean validate) {
        // This boolean becomes true if we encounter a command, which indicates we need
        // to stop parsing (the parsing of the command will be done in a sub JCommander
        // object)
        boolean commandParsed = false;
        int i = 0;
        boolean isDashDash = false; // once we encounter --, everything goes into the main parameter
        while (i < args.length && !commandParsed) {
            String arg = args[i];
            String a = trim(arg);
            args[i] = a;
            p("Parsing arg: " + a);

            JCommander jc = findCommandByAlias(arg);
            int increment = 1;
            if (!isDashDash && !"--".equals(a) && isOption(a) && jc == null) {
                //
                // Option
                //
                ParameterDescription pd = findParameterDescription(a);

                if (pd != null) {
                    if (pd.getParameter().password()) {
                        increment = processPassword(args, i, pd, validate);
                    } else {
                        if (pd.getParameter().variableArity()) {
                            //
                            // Variable arity?
                            //
                            increment = processVariableArity(args, i, pd, validate);
                        } else {
                            //
                            // Regular option
                            //
                            Class<?> fieldType = pd.getParameterized().getType();

                            // Boolean, set to true as soon as we see it, unless it specified
                            // an arity of 1, in which case we need to read the next value
                            if ((fieldType == boolean.class || fieldType == Boolean.class)
                                    && pd.getParameter().arity() == -1) {
                                // Flip the value this boolean was initialized with
                                Boolean value = (Boolean) pd.getParameterized().get(pd.getObject());
                                pd.addValue(value ? "false" : "true");
                                requiredFields.remove(pd.getParameterized());
                            } else {
                                increment = processFixedArity(args, i, pd, validate, fieldType);
                            }
                            // If it's a help option, remember for later
                            if (pd.isHelp()) {
                                helpWasSpecified = true;
                            }
                        }
                    }
                } else {
                    if (options.acceptUnknownOptions) {
                        unknownArgs.add(arg);
                        i++;
                        while (i < args.length && !isOption(args[i])) {
                            unknownArgs.add(args[i++]);
                        }
                        increment = 0;
                    } else {
                        throw new ParameterException("Unknown option: " + arg);
                    }
                }
            } else {
                //
                // Main parameter
                //
                if ("--".equals(arg) && !isDashDash) {
                    isDashDash = true;
                }
                else if (commands.isEmpty()) {
                    //
                    // Regular (non-command) parsing
                    //
                    List mp = getMainParameter(arg);
                    String value = a; // If there's a non-quoted version, prefer that one
                    Object convertedValue = value;

                    if (mainParameter.getGenericType() instanceof ParameterizedType) {
                        ParameterizedType p = (ParameterizedType) mainParameter.getGenericType();
                        Type cls = p.getActualTypeArguments()[0];
                        if (cls instanceof Class) {
                            convertedValue = convertValue(mainParameter, (Class) cls, null, value);
                        }
                    }
                    
                    for(final Class<? extends IParameterValidator> validator : mainParameterAnnotation.validateWith() ) {
                        ParameterDescription.validateParameter(mainParameterDescription,
                        	validator,
                            "Default", value);
                    }

                    mainParameterDescription.setAssigned(true);
                    mp.add(convertedValue);
                } else {
                    //
                    // Command parsing
                    //
                    if (jc == null && validate) {
                        throw new MissingCommandException("Expected a command, got " + arg, arg);
                    } else if (jc != null) {
                        parsedCommand = jc.programName.name;
                        parsedAlias = arg; //preserve the original form

                        // Found a valid command, ask it to parse the remainder of the arguments.
                        // Setting the boolean commandParsed to true will force the current
                        // loop to end.
                        jc.parse(validate, subArray(args, i + 1));
                        commandParsed = true;
                    }
                }
            }
            i += increment;
        }

        // Mark the parameter descriptions held in fields as assigned
        for (ParameterDescription parameterDescription : descriptions.values()) {
            if (parameterDescription.isAssigned()) {
                fields.get(parameterDescription.getParameterized()).setAssigned(true);
            }
        }

    }

    private class DefaultVariableArity implements IVariableArity {

        @Override
        public int processVariableArity(String optionName, String[] options) {
            int i = 0;
            while (i < options.length && !isOption(options[i])) {
                i++;
            }
            return i;
        }
    }

    private final IVariableArity DEFAULT_VARIABLE_ARITY = new DefaultVariableArity();

    private final int determineArity(String[] args, int index, ParameterDescription pd, IVariableArity va) {
        List<String> currentArgs = Lists.newArrayList();
        for (int j = index + 1; j < args.length; j++) {
            currentArgs.add(args[j]);
        }
        return va.processVariableArity(pd.getParameter().names()[0],
                currentArgs.toArray(new String[0]));
    }

    /**
     * @return the number of options that were processed.
     */
    private int processPassword(String[] args, int index, ParameterDescription pd, boolean validate) {
        final int passwordArity = determineArity(args, index, pd, DEFAULT_VARIABLE_ARITY);
        if (passwordArity == 0) {
            // password option with password not specified, use the Console to retrieve the password
            char[] password = readPassword(pd.getDescription(), pd.getParameter().echoInput());
            pd.addValue(new String(password));
            requiredFields.remove(pd.getParameterized());
            return 1;
        } else if (passwordArity == 1) {
            // password option with password specified
            return processFixedArity(args, index, pd, validate, List.class, 1);
        } else {
            throw new ParameterException("Password parameter must have at most 1 argument.");
        }
    }

    /**
     * @return the number of options that were processed.
     */
    private int processVariableArity(String[] args, int index, ParameterDescription pd, boolean validate) {
        Object arg = pd.getObject();
        IVariableArity va;
        if (!(arg instanceof IVariableArity)) {
            va = DEFAULT_VARIABLE_ARITY;
        } else {
            va = (IVariableArity) arg;
        }

        int arity = determineArity(args, index, pd, va);
        int result = processFixedArity(args, index, pd, validate, List.class, arity);
        return result;
    }

    private int processFixedArity(String[] args, int index, ParameterDescription pd, boolean validate,
                                  Class<?> fieldType) {
        // Regular parameter, use the arity to tell use how many values
        // we need to consume
        int arity = pd.getParameter().arity();
        int n = (arity != -1 ? arity : 1);

        return processFixedArity(args, index, pd, validate, fieldType, n);
    }

    private int processFixedArity(String[] args, int originalIndex, ParameterDescription pd, boolean validate,
                                  Class<?> fieldType, int arity) {
        int index = originalIndex;
        String arg = args[index];
        // Special case for boolean parameters of arity 0
        if (arity == 0 &&
                (Boolean.class.isAssignableFrom(fieldType)
                        || boolean.class.isAssignableFrom(fieldType))) {
            // Flip the value this boolean was initialized with
            Boolean value = (Boolean) pd.getParameterized().get(pd.getObject());
            pd.addValue(value ? "false" : "true");
            requiredFields.remove(pd.getParameterized());
        } else if (arity == 0) {
            throw new ParameterException("Expected a value after parameter " + arg);

        } else if (index < args.length - 1) {
            int offset = "--".equals(args[index + 1]) ? 1 : 0;

            Object finalValue = null;
            if (index + arity < args.length) {
                for (int j = 1; j <= arity; j++) {
                    String value = trim(args[index + j + offset]);
                    finalValue = pd.addValue(arg, value, false, validate, j - 1);
                    requiredFields.remove(pd.getParameterized());
                }

                if (finalValue != null && validate) {
                  pd.validateValueParameter(arg, finalValue);
                }
                index += arity + offset;
            } else {
                throw new ParameterException("Expected " + arity + " values after " + arg);
            }
        } else {
            throw new ParameterException("Expected a value after parameter " + arg);
        }

        return arity + 1;
    }

    /**
     * Invoke Console.readPassword through reflection to avoid depending
     * on Java 6.
     */
    private char[] readPassword(String description, boolean echoInput) {
        getConsole().print(description + ": ");
        return getConsole().readPassword(echoInput);
    }

    private String[] subArray(String[] args, int index) {
        int l = args.length - index;
        String[] result = new String[l];
        System.arraycopy(args, index, result, 0, l);

        return result;
    }

    /**
     * @return the field that's meant to receive all the parameters that are not options.
     *
     * @param arg the arg that we're about to add (only passed here to output a meaningful
     * error message).
     */
    private List<?> getMainParameter(String arg) {
        if (mainParameter == null) {
            throw new ParameterException(
                    "Was passed main parameter '" + arg + "' but no main parameter was defined in your arg class");
        }

        List<?> result = (List<?>) mainParameter.get(mainParameterObject);
        if (result == null) {
            result = Lists.newArrayList();
            if (!List.class.isAssignableFrom(mainParameter.getType())) {
                throw new ParameterException("Main parameter field " + mainParameter
                        + " needs to be of type List, not " + mainParameter.getType());
            }
            mainParameter.set(mainParameterObject, result);
        }
        if (firstTimeMainParameter) {
            result.clear();
            firstTimeMainParameter = false;
        }
        return result;
    }

    public String getMainParameterDescription() {
        if (descriptions == null) createDescriptions();
        return mainParameterAnnotation != null ? mainParameterAnnotation.description()
                : null;
    }

    /**
     * Set the program name (used only in the usage).
     */
    public void setProgramName(String name) {
        setProgramName(name, new String[0]);
    }

    /**
     * Get the program name (used only in the usage).
     */
    public String getProgramName(){
        return programName == null ? null : programName.getName();
    }

    /**
     * Set the program name
     *
     * @param name    program name
     * @param aliases aliases to the program name
     */
    public void setProgramName(String name, String... aliases) {
        programName = new ProgramName(name, Arrays.asList(aliases));
    }

    /**
     * Display the usage for this command.
     */
    public void usage(String commandName) {
        StringBuilder sb = new StringBuilder();
        usage(commandName, sb);
        getConsole().println(sb.toString());
    }

    /**
     * Store the help for the command in the passed string builder.
     */
    public void usage(String commandName, StringBuilder out) {
        usage(commandName, out, "");
    }

    /**
     * Store the help for the command in the passed string builder, indenting
     * every line with "indent".
     */
    public void usage(String commandName, StringBuilder out, String indent) {
        String description = getCommandDescription(commandName);
        JCommander jc = findCommandByAlias(commandName);
        if (description != null) {
            out.append(indent).append(description);
            out.append("\n");
        }
        jc.usage(out, indent);
    }

    /**
     * @return the description of the command.
     */
    public String getCommandDescription(String commandName) {
        JCommander jc = findCommandByAlias(commandName);
        if (jc == null) {
            throw new ParameterException("Asking description for unknown command: " + commandName);
        }

        Object arg = jc.getObjects().get(0);
        Parameters p = arg.getClass().getAnnotation(Parameters.class);
        ResourceBundle bundle = null;
        String result = null;
        if (p != null) {
            result = p.commandDescription();
            String bundleName = p.resourceBundle();
            if (!"".equals(bundleName)) {
                bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
            } else {
                bundle = options.bundle;
            }

            if (bundle != null) {
                String descriptionKey = p.commandDescriptionKey();
                if (!"".equals(descriptionKey)) {
                    result = getI18nString(bundle, descriptionKey, p.commandDescription());
                }
            }
        }

        return result;
    }

    /**
     * @return The internationalized version of the string if available, otherwise
     * return def.
     */
    private String getI18nString(ResourceBundle bundle, String key, String def) {
        String s = bundle != null ? bundle.getString(key) : null;
        return s != null ? s : def;
    }

    /**
     * Display the help on System.out.
     */
    public void usage() {
        StringBuilder sb = new StringBuilder();
        usage(sb);
        getConsole().println(sb.toString());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private JCommander jCommander = new JCommander();
        private String[] args = null;

        public Builder() {
        }

        /**
         * Adds the provided arg object to the set of objects that this commander
         * will parse arguments into.
         *
         * @param o The arg object expected to contain {@link Parameter}
         * annotations. If <code>object</code> is an array or is {@link Iterable},
         * the child objects will be added instead.
         */
        public Builder addObject(Object o) {
            jCommander.addObject(o);
            return this;
        }

        /**
         * Sets the {@link ResourceBundle} to use for looking up descriptions.
         * Set this to <code>null</code> to use description text directly.
         */
        public Builder resourceBundle(ResourceBundle bundle) {
            jCommander.setDescriptionsBundle(bundle);
            return this;
        }

        public Builder args(String[] args) {
            this.args = args;
            return this;
        }

        /**
         * Disables expanding {@code @file}.
         *
         * JCommander supports the {@code @file} syntax, which allows you to put all your options
         * into a file and pass this file as parameter @param expandAtSign whether to expand {@code @file}.
         */
        public Builder expandAtSign(Boolean expand) {
            jCommander.setExpandAtSign(expand);
            return this;
        }

        /**
         * Set the program name (used only in the usage).
         */
        public Builder programName(String name) {
            jCommander.setProgramName(name);
            return this;
        }

        public Builder columnSize(int columnSize) {
            jCommander.setColumnSize(columnSize);
            return this;
        }

        /**
         * Define the default provider for this instance.
         */
        public Builder defaultProvider(IDefaultProvider provider) {
            jCommander.setDefaultProvider(provider);
            return this;
        }

        /**
         * Adds a factory to lookup string converters. The added factory is used prior to previously added factories.
         * @param factory the factory determining string converters
         */
        public Builder addConverterFactory(IStringConverterFactory factory) {
            jCommander.addConverterFactory(factory);
            return this;
        }

        public Builder verbose(int verbose) {
            jCommander.setVerbose(verbose);
            return this;
        }

        public Builder allowAbbreviatedOptions(boolean b) {
            jCommander.setAllowAbbreviatedOptions(b);
            return this;
        }

        public Builder acceptUnknownOptions(boolean b) {
            jCommander.setAcceptUnknownOptions(b);
            return this;
        }

        public Builder allowParameterOverwriting(boolean b) {
            jCommander.setAllowParameterOverwriting(b);
            return this;
        }

        public Builder atFileCharset(Charset charset) {
            jCommander.setAtFileCharset(charset);
            return this;
        }

        public Builder addConverterInstanceFactory(IStringConverterInstanceFactory factory) {
            jCommander.addConverterInstanceFactory(factory);
            return this;
        }

        public Builder addCommand(Object command) {
            jCommander.addCommand(command);
            return this;
        }

        public Builder addCommand(String name, Object command, String... aliases) {
            jCommander.addCommand(name, command, aliases);
            return this;
        }

        public JCommander build() {
            if (args != null) {
                jCommander.parse(args);
            }
            return jCommander;
        }
    }


    /**
     * Store the help in the passed string builder.
     */
    public void usage(StringBuilder out) {
        usage(out, "");
    }

    public void usage(StringBuilder out, String indent) {
        if (descriptions == null) createDescriptions();
        boolean hasCommands = !commands.isEmpty();
        boolean hasOptions = !descriptions.isEmpty();

        //indenting
        int descriptionIndent = 6;
        int indentCount = indent.length() + descriptionIndent;

        //
        // First line of the usage
        //
        String programName = this.programName != null ? this.programName.getDisplayName() : "<main class>";
        StringBuilder mainLine = new StringBuilder();
        mainLine.append(indent).append("Usage: ").append(programName);
        if (hasOptions) mainLine.append(" [options]");
        if (hasCommands) mainLine.append(indent).append(" [command] [command options]");
        if (mainParameterDescription != null) {
            mainLine.append(" ").append(mainParameterDescription.getDescription());
        }
        wrapDescription(out, indentCount, mainLine.toString());
        out.append("\n");

        //
        // Align the descriptions at the "longestName" column
        //
        int longestName = 0;
        List<ParameterDescription> sorted = Lists.newArrayList();
        for (ParameterDescription pd : fields.values()) {
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
        Collections.sort(sorted, getParameterDescriptionComparator());

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
            for (Map.Entry<ProgramName, JCommander> commands : this.commands.entrySet()) {
                Object arg = commands.getValue().getObjects().get(0);
                Parameters p = arg.getClass().getAnnotation(Parameters.class);
                if (p == null || !p.hidden()) {
                    ProgramName progName = commands.getKey();
                    String dispName = progName.getDisplayName();
                    String description = getCommandDescription(progName.getName());
                    wrapDescription(out, indentCount + descriptionIndent,
                            indent + "    " + dispName + "      " + description);
                    out.append("\n");

                    // Options for this command
                    JCommander jc = findCommandByAlias(progName.getName());
                    jc.usage(out, indent + "      ");
                    out.append("\n");
                }
            }
        }
    }

    private Comparator<? super ParameterDescription> getParameterDescriptionComparator() {
        return options.parameterDescriptionComparator;
    }

    public void setParameterDescriptionComparator(Comparator<? super ParameterDescription> c) {
        options.parameterDescriptionComparator = c;
    }

    public void setColumnSize(int columnSize) {
        options.columnSize = columnSize;
    }

    public int getColumnSize() {
        return options.columnSize;
    }

    /**
     * Wrap a potentially long line to {@link #getColumnSize()}.
     *
     * @param out         the output
     * @param indent      the indentation in spaces for lines after the first line.
     * @param description the text to wrap. No extra spaces are inserted before {@code
     *                    description}. If the first line needs to be indented prepend the
     *                    correct number of spaces to {@code description}.
     */
    private void wrapDescription(StringBuilder out, int indent, String description) {
        int max = getColumnSize();
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
     * @return a Collection of all the \@Parameter annotations found on the
     * target class. This can be used to display the usage() in a different
     * format (e.g. HTML).
     */
    public List<ParameterDescription> getParameters() {
        return new ArrayList<>(fields.values());
    }

    /**
     * @return the main parameter description or null if none is defined.
     */
    public ParameterDescription getMainParameter() {
        return mainParameterDescription;
    }

    private void p(String string) {
        if (options.verbose > 0 || System.getProperty(JCommander.DEBUG_PROPERTY) != null) {
            getConsole().println("[JCommander] " + string);
        }
    }

    /**
     * Define the default provider for this instance.
     */
    public void setDefaultProvider(IDefaultProvider defaultProvider) {
        options.defaultProvider = defaultProvider;
    }

    /**
     * Adds a factory to lookup string converters. The added factory is used prior to previously added factories.
     * @param converterFactory the factory determining string converters
     */
    public void addConverterFactory(final IStringConverterFactory converterFactory) {
        addConverterInstanceFactory(new IStringConverterInstanceFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public IStringConverter<?> getConverterInstance(Parameter parameter, Class<?> forType, String optionName) {
                final Class<? extends IStringConverter<?>> converterClass = converterFactory.getConverter(forType);
                try {
                    if(optionName == null) {
                        optionName = parameter.names().length > 0 ? parameter.names()[0] : "[Main class]";
                    }
                    return converterClass != null ? instantiateConverter(optionName, converterClass) : null;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new ParameterException(e);
                }
            }
        });
    }

    /**
     * Adds a factory to lookup string converters. The added factory is used prior to previously added factories.
     * @param converterInstanceFactory the factory generating string converter instances
     */
    public void addConverterInstanceFactory(IStringConverterInstanceFactory converterInstanceFactory) {
        options.converterInstanceFactories.add(0, converterInstanceFactory);
    }

    private IStringConverter<?> findConverterInstance(Parameter parameter, Class<?> forType, String optionName) {
        for (IStringConverterInstanceFactory f : options.converterInstanceFactories) {
            IStringConverter<?> result = f.getConverterInstance(parameter, forType, optionName);
            if (result != null) return result;
        }

        return null;
    }

    /**
     * @param type The type of the actual parameter
     * @param optionName
     * @param value The value to convert
     */
    public Object convertValue(final Parameterized parameterized, Class type, String optionName, String value) {
        final Parameter annotation = parameterized.getParameter();

        // Do nothing if it's a @DynamicParameter
        if (annotation == null) return value;

        if(optionName == null) {
            optionName = annotation.names().length > 0 ? annotation.names()[0] : "[Main class]";
        }

        IStringConverter<?> converter = null;
        if (type.isAssignableFrom(List.class)) {
            // If a list converter was specified, pass the value to it for direct conversion
            converter = tryInstantiateConverter(optionName, annotation.listConverter());
        }
        if (type.isAssignableFrom(List.class) && converter == null) {
            // No list converter: use the single value converter and pass each parsed value to it individually
            final IParameterSplitter splitter = tryInstantiateConverter(null, annotation.splitter());
            converter = new DefaultListConverter(splitter, new IStringConverter() {
                @Override
                public Object convert(String value) {
                    final Type genericType = parameterized.findFieldGenericType();
                    return convertValue(parameterized, genericType instanceof Class ? (Class) genericType : String.class, null, value);
                }
            });
        }

        if (converter == null) {
            converter = tryInstantiateConverter(optionName, annotation.converter());
        }
        if (converter == null) {
            converter = findConverterInstance(annotation, type, optionName);
        }
        if (converter == null && type.isEnum()) {
            converter = new EnumConverter(optionName, type);
        }
        if (converter == null) {
            converter = new StringConverter();
        }
        return converter.convert(value);
    }

    private static <T> T tryInstantiateConverter(String optionName, Class<T> converterClass) {
        if (converterClass == NoConverter.class || converterClass == null) {
            return null;
        }
        try {
            return instantiateConverter(optionName, converterClass);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignore) {
            return null;
        }
    }

    private static <T> T instantiateConverter(String optionName, Class<? extends T> converterClass)
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Constructor<T> ctor = null;
        Constructor<T> stringCtor = null;
        for (Constructor<T> c : (Constructor<T>[]) converterClass.getDeclaredConstructors()) {
            c.setAccessible(true);
            Class<?>[] types = c.getParameterTypes();
            if (types.length == 1 && types[0].equals(String.class)) {
                stringCtor = c;
            } else if (types.length == 0) {
                ctor = c;
            }
        }

        return stringCtor != null
                ? stringCtor.newInstance(optionName)
                : ctor != null
                ? ctor.newInstance()
                : null;
    }

    /**
     * Add a command object.
     */
    public void addCommand(String name, Object object) {
        addCommand(name, object, new String[0]);
    }

    public void addCommand(Object object) {
        Parameters p = object.getClass().getAnnotation(Parameters.class);
        if (p != null && p.commandNames().length > 0) {
            for (String commandName : p.commandNames()) {
                addCommand(commandName, object);
            }
        } else {
            throw new ParameterException("Trying to add command " + object.getClass().getName()
                    + " without specifying its names in @Parameters");
        }
    }

    /**
     * Add a command object and its aliases.
     */
    public void addCommand(String name, Object object, String... aliases) {
        JCommander jc = new JCommander(options);
        jc.addObject(object);
        jc.createDescriptions();
        jc.setProgramName(name, aliases);
        ProgramName progName = jc.programName;
        commands.put(progName, jc);

    /*
    * Register aliases
    */
        //register command name as an alias of itself for reverse lookup
        //Note: Name clash check is intentionally omitted to resemble the
        //     original behaviour of clashing commands.
        //     Aliases are, however, are strictly checked for name clashes.
        aliasMap.put(new StringKey(name), progName);
        for (String a : aliases) {
            IKey alias = new StringKey(a);
            //omit pointless aliases to avoid name clash exception
            if (!alias.equals(name)) {
                ProgramName mappedName = aliasMap.get(alias);
                if (mappedName != null && !mappedName.equals(progName)) {
                    throw new ParameterException("Cannot set alias " + alias
                            + " for " + name
                            + " command because it has already been defined for "
                            + mappedName.name + " command");
                }
                aliasMap.put(alias, progName);
            }
        }
    }

    public Map<String, JCommander> getCommands() {
        Map<String, JCommander> res = Maps.newLinkedHashMap();
        for (Map.Entry<ProgramName, JCommander> entry : commands.entrySet()) {
            res.put(entry.getKey().name, entry.getValue());
        }
        return res;
    }

    public String getParsedCommand() {
        return parsedCommand;
    }

    /**
     * The name of the command or the alias in the form it was
     * passed to the command line. <code>null</code> if no
     * command or alias was specified.
     *
     * @return Name of command or alias passed to command line. If none passed: <code>null</code>.
     */
    public String getParsedAlias() {
        return parsedAlias;
    }

    /**
     * @return n spaces
     */
    private String s(int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(" ");
        }

        return result.toString();
    }

    /**
     * @return the objects that JCommander will fill with the result of
     * parsing the command line.
     */
    public List<Object> getObjects() {
        return objects;
    }

    private ParameterDescription findParameterDescription(String arg) {
        return FuzzyMap.findInMap(descriptions, new StringKey(arg),
                options.caseSensitiveOptions, options.allowAbbreviatedOptions);
    }

    private JCommander findCommand(ProgramName name) {
        return FuzzyMap.findInMap(commands, name,
                options.caseSensitiveOptions, options.allowAbbreviatedOptions);
    }

    private ProgramName findProgramName(String name) {
        return FuzzyMap.findInMap(aliasMap, new StringKey(name),
                options.caseSensitiveOptions, options.allowAbbreviatedOptions);
    }

    /*
    * Reverse lookup JCommand object by command's name or its alias
    */
    private JCommander findCommandByAlias(String commandOrAlias) {
        ProgramName progName = findProgramName(commandOrAlias);
        if (progName == null) {
            return null;
        }
        JCommander jc = findCommand(progName);
        if (jc == null) {
            throw new IllegalStateException(
                    "There appears to be inconsistency in the internal command database. " +
                            " This is likely a bug. Please report.");
        }
        return jc;
    }

    /**
     * Encapsulation of either a main application or an individual command.
     */
    private static final class ProgramName implements IKey {
        private final String name;
        private final List<String> aliases;

        ProgramName(String name, List<String> aliases) {
            this.name = name;
            this.aliases = aliases;
        }

        @Override
        public String getName() {
            return name;
        }

        private String getDisplayName() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            if (!aliases.isEmpty()) {
                sb.append("(");
                Iterator<String> aliasesIt = aliases.iterator();
                while (aliasesIt.hasNext()) {
                    sb.append(aliasesIt.next());
                    if (aliasesIt.hasNext()) {
                        sb.append(",");
                    }
                }
                sb.append(")");
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ProgramName other = (ProgramName) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        /*
         * Important: ProgramName#toString() is used by longestName(Collection) function
         * to format usage output.
         */
        @Override
        public String toString() {
            return getDisplayName();

        }
    }

    public void setVerbose(int verbose) {
        options.verbose = verbose;
    }

    public void setCaseSensitiveOptions(boolean b) {
        options.caseSensitiveOptions = b;
    }

    public void setAllowAbbreviatedOptions(boolean b) {
        options.allowAbbreviatedOptions = b;
    }

    public void setAcceptUnknownOptions(boolean b) {
        options.acceptUnknownOptions = b;
    }

    public List<String> getUnknownOptions() {
        return unknownArgs;
    }

    public void setAllowParameterOverwriting(boolean b) {
        options.allowParameterOverwriting = b;
    }

    public boolean isParameterOverwritingAllowed() {
        return options.allowParameterOverwriting;
    }

    /**
     * Sets the charset used to expand {@code @files}.
     * @param charset the charset
     */
    public void setAtFileCharset(Charset charset) {
        options.atFileCharset = charset;
    }

}
