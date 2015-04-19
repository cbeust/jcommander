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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.beust.jcommander.FuzzyMap.IKey;
import com.beust.jcommander.converters.IParameterSplitter;
import com.beust.jcommander.converters.NoConverter;
import com.beust.jcommander.converters.StringConverter;
import com.beust.jcommander.internal.Console;
import com.beust.jcommander.internal.DefaultConsole;
import com.beust.jcommander.internal.DefaultConverterFactory;
import com.beust.jcommander.internal.JDK6Console;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.beust.jcommander.internal.Nullable;

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
  private Map<IKey, ParameterDescription> m_descriptions;

  /**
   * The objects that contain fields annotated with @Parameter.
   */
  private List<Object> m_objects = Lists.newArrayList();

  private boolean m_firstTimeMainParameter = true;

  /**
   * This field/method will contain whatever command line parameter is not an option.
   * It is expected to be a List<String>.
   */
  private Parameterized m_mainParameter = null;

  /**
   * The object on which we found the main parameter field.
   */
  private Object m_mainParameterObject;

  /**
   * The annotation found on the main parameter field.
   */
  private Parameter m_mainParameterAnnotation;

  private ParameterDescription m_mainParameterDescription;

  /**
   * A set of all the parameterizeds that are required. During the reflection phase,
   * this field receives all the fields that are annotated with required=true
   * and during the parsing phase, all the fields that are assigned a value
   * are removed from it. At the end of the parsing phase, if it's not empty,
   * then some required fields did not receive a value and an exception is
   * thrown.
   */
  private Map<Parameterized, ParameterDescription> m_requiredFields = Maps.newHashMap();

  /**
   * A map of all the parameterized fields/methods.
   */
  private Map<Parameterized, ParameterDescription> m_fields = Maps.newHashMap();

  private ResourceBundle m_bundle;

  /**
   * A default provider returns default values for the parameters.
   */
  private IDefaultProvider m_defaultProvider;

  /**
   * List of commands and their instance.
   */
  private Map<ProgramName, JCommander> m_commands = Maps.newLinkedHashMap();

  /**
   * Alias database for reverse lookup
   */
  private Map<IKey, ProgramName> aliasMap = Maps.newLinkedHashMap();

  /**
   * The name of the command after the parsing has run.
   */
  private String m_parsedCommand;

  /**
   * The name of command or alias as it was passed to the
   * command line
   */
  private String m_parsedAlias;

  private ProgramName m_programName;

  private Comparator<? super ParameterDescription> m_parameterDescriptionComparator
      = new Comparator<ParameterDescription>() {
        @Override
        public int compare(ParameterDescription p0, ParameterDescription p1) {
          return p0.getLongestName().compareTo(p1.getLongestName());
        }
      };

  private int m_columnSize = 79;

  private boolean m_helpWasSpecified;

  private List<String> m_unknownArgs = Lists.newArrayList();
  private boolean m_acceptUnknownOptions = false;
  private boolean m_allowParameterOverwriting = false;
  
  private static Console m_console;

  /**
   * The factories used to look up string converters.
   */
  private static LinkedList<IStringConverterFactory> CONVERTER_FACTORIES = Lists.newLinkedList();

  static {
    CONVERTER_FACTORIES.addFirst(new DefaultConverterFactory());
  };

  /**
   * Creates a new un-configured JCommander object.
   */
  public JCommander() {
  }

  /**
   * @param object The arg object expected to contain {@link Parameter} annotations.
   */
  public JCommander(Object object) {
    addObject(object);
    createDescriptions();
  }

  /**
   * @param object The arg object expected to contain {@link Parameter} annotations.
   * @param bundle The bundle to use for the descriptions. Can be null.
   */
  public JCommander(Object object, @Nullable ResourceBundle bundle) {
    addObject(object);
    setDescriptionsBundle(bundle);
  }

  /**
   * @param object The arg object expected to contain {@link Parameter} annotations.
   * @param bundle The bundle to use for the descriptions. Can be null.
   * @param args The arguments to parse (optional).
   */
  public JCommander(Object object, ResourceBundle bundle, String... args) {
    addObject(object);
    setDescriptionsBundle(bundle);
    parse(args);
  }

  /**
   * @param object The arg object expected to contain {@link Parameter} annotations.
   * @param args The arguments to parse (optional).
   */
  public JCommander(Object object, String... args) {
    addObject(object);
    parse(args);
  }
  
  public static Console getConsole() {
    if (m_console == null) {
      try {
        Method consoleMethod = System.class.getDeclaredMethod("console", new Class<?>[0]);
        Object console = consoleMethod.invoke(null, new Object[0]);
        m_console = new JDK6Console(console);
      } catch (Throwable t) {
        m_console = new DefaultConsole();
      }
    }
    return m_console;
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
        m_objects.add(o);
      }
    } else if (object.getClass().isArray()) {
      // Array
      for (Object o : (Object[]) object) {
        m_objects.add(o);
      }
    } else {
      // Single object
      m_objects.add(object);
    }
  }

  /**
   * Sets the {@link ResourceBundle} to use for looking up descriptions.
   * Set this to <code>null</code> to use description text directly.
   */
  // declared final since this is invoked from constructors
  public final void setDescriptionsBundle(ResourceBundle bundle) {
    m_bundle = bundle;
  }

  /**
   * Parse and validate the command line parameters.
   */
  public void parse(String... args) {
    parse(true /* validate */, args);
  }

  /**
   * Parse the command line parameters without validating them.
   */
  public void parseWithoutValidation(String... args) {
    parse(false /* no validation */, args);
  }

  private void parse(boolean validate, String... args) {
    StringBuilder sb = new StringBuilder("Parsing \"");
    sb.append(join(args).append("\"\n  with:").append(join(m_objects.toArray())));
    p(sb.toString());

    if (m_descriptions == null) createDescriptions();
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
    if (m_defaultProvider != null) {
      for (ParameterDescription pd : m_descriptions.values()) {
        initializeDefaultValue(pd);
      }

      for (Map.Entry<ProgramName, JCommander> entry : m_commands.entrySet()) {
        entry.getValue().initializeDefaultValues();
      }
    }
  }

  /**
   * Make sure that all the required parameters have received a value.
   */
  private void validateOptions() {
    // No validation if we found a help parameter
    if (m_helpWasSpecified) {
      return;
    }

    if (! m_requiredFields.isEmpty()) {
      StringBuilder missingFields = new StringBuilder();
      for (ParameterDescription pd : m_requiredFields.values()) {
        missingFields.append(pd.getNames()).append(" ");
      }
      throw new ParameterException("The following "
            + pluralize(m_requiredFields.size(), "option is required: ", "options are required: ")
            + missingFields);
    }

    if (m_mainParameterDescription != null) {
      if (m_mainParameterDescription.getParameter().required() &&
          !m_mainParameterDescription.isAssigned()) {
        throw new ParameterException("Main parameters are required (\""
            + m_mainParameterDescription.getDescription() + "\")");
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

      if (arg.startsWith("@")) {
        String fileName = arg.substring(1);
        vResult1.addAll(readFile(fileName));
      }
      else {
        List<String> expanded = expandDynamicArg(arg);
        vResult1.addAll(expanded);
      }
    }

    // Expand separators
    //
    List<String> vResult2 = Lists.newArrayList();
    for (int i = 0; i < vResult1.size(); i++) {
      String arg = vResult1.get(i);
      String[] v1 = vResult1.toArray(new String[0]);
      if (isOption(v1, arg)) {
        String sep = getSeparatorFor(v1, arg);
        if (! " ".equals(sep)) {
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
    for (ParameterDescription pd : m_descriptions.values()) {
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

  private boolean isOption(String[] args, String arg) {
    String prefixes = getOptionPrefixes(args, arg);
    return arg.length() > 0 && prefixes.indexOf(arg.charAt(0)) >= 0;
  }

  private ParameterDescription getPrefixDescriptionFor(String arg) {
    for (Map.Entry<IKey, ParameterDescription> es : m_descriptions.entrySet()) {
      if (arg.startsWith(es.getKey().getName())) return es.getValue();
    }

    return null;
  }

  /**
   * If arg is an option, we can look it up directly, but if it's a value,
   * we need to find the description for the option that precedes it.
   */
  private ParameterDescription getDescriptionFor(String[] args, String arg) {
    ParameterDescription result = getPrefixDescriptionFor(arg);
    if (result != null) return result;

    for (String a : args) {
      ParameterDescription pd = getPrefixDescriptionFor(arg);
      if (pd != null) result = pd;
      if (a.equals(arg)) return result;
    }

    throw new ParameterException("Unknown parameter: " + arg);
  }

  private String getSeparatorFor(String[] args, String arg) {
    ParameterDescription pd = getDescriptionFor(args, arg);

    // Could be null if only main parameters were passed
    if (pd != null) {
      Parameters p = pd.getObject().getClass().getAnnotation(Parameters.class);
      if (p != null) return p.separators();
    }

    return " ";
  }

  private String getOptionPrefixes(String[] args, String arg) {
    ParameterDescription pd = getDescriptionFor(args, arg);

    // Could be null if only main parameters were passed
    if (pd != null) {
      Parameters p = pd.getObject().getClass()
          .getAnnotation(Parameters.class);
      if (p != null) return p.optionPrefixes();
    }
    String result = Parameters.DEFAULT_OPTION_PREFIXES;

    // See if any of the objects contains a @Parameters(optionPrefixes)
    StringBuilder sb = new StringBuilder();
    for (Object o : m_objects) {
      Parameters p = o.getClass().getAnnotation(Parameters.class);
      if (p != null && !Parameters.DEFAULT_OPTION_PREFIXES.equals(p.optionPrefixes())) {
        sb.append(p.optionPrefixes());
      }
    }

    if (! Strings.isStringEmpty(sb.toString())) {
      result = sb.toString();
    }

    return result;
  }

  /**
   * Reads the file specified by filename and returns the file content as a string.
   * End of lines are replaced by a space.
   *
   * @param fileName the command line filename
   * @return the file content as a string.
   */
  private static List<String> readFile(String fileName) {
    List<String> result = Lists.newArrayList();

    try {
      BufferedReader bufRead = new BufferedReader(new FileReader(fileName));

      String line;

      // Read through file one line at time. Print line # and line
      while ((line = bufRead.readLine()) != null) {
        // Allow empty lines and # comments in these at files
        if (line.length() > 0 && ! line.trim().startsWith("#")) {
            result.add(line);
        }
      }

      bufRead.close();
    }
    catch (IOException e) {
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
    m_descriptions = Maps.newHashMap();

    for (Object object : m_objects) {
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
          if (m_mainParameter != null) {
            throw new ParameterException("Only one @Parameter with no names attribute is"
                + " allowed, found:" + m_mainParameter + " and " + parameterized);
          }
          m_mainParameter = parameterized;
          m_mainParameterObject = object;
          m_mainParameterAnnotation = p;
          m_mainParameterDescription =
              new ParameterDescription(object, p, parameterized, m_bundle, this);
        } else {
          ParameterDescription pd =
              new ParameterDescription(object, p, parameterized, m_bundle, this);
          for (String name : p.names()) {
            if (m_descriptions.containsKey(new StringKey(name))) {
              throw new ParameterException("Found the option " + name + " multiple times");
            }
            p("Adding description for " + name);
            m_fields.put(parameterized, pd);
            m_descriptions.put(new StringKey(name), pd);

            if (p.required()) m_requiredFields.put(parameterized, pd);
          }
        }
      } else if (parameterized.getDelegateAnnotation() != null) {
        //
        // @ParametersDelegate
        //
        Object delegateObject = parameterized.get(object);
        if (delegateObject == null){
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
          if (m_descriptions.containsKey(name)) {
            throw new ParameterException("Found the option " + name + " multiple times");
          }
          p("Adding description for " + name);
          ParameterDescription pd =
              new ParameterDescription(object, dp, parameterized, m_bundle, this);
          m_fields.put(parameterized, pd);
          m_descriptions.put(new StringKey(name), pd);
    
          if (dp.required()) m_requiredFields.put(parameterized, pd);
        }
      }
    }

//    while (!Object.class.equals(cls)) {
//      for (Field f : cls.getDeclaredFields()) {
//        p("Field:" + cls.getSimpleName() + "." + f.getName());
//        f.setAccessible(true);
//        Annotation annotation = f.getAnnotation(Parameter.class);
//        Annotation delegateAnnotation = f.getAnnotation(ParametersDelegate.class);
//        Annotation dynamicParameter = f.getAnnotation(DynamicParameter.class);
//        if (annotation != null) {
//          //
//          // @Parameter
//          //
//          Parameter p = (Parameter) annotation;
//          if (p.names().length == 0) {
//            p("Found main parameter:" + f);
//            if (m_mainParameterField != null) {
//              throw new ParameterException("Only one @Parameter with no names attribute is"
//                  + " allowed, found:" + m_mainParameterField + " and " + f);
//            }
//            m_mainParameterField = parameterized;
//            m_mainParameterObject = object;
//            m_mainParameterAnnotation = p;
//            m_mainParameterDescription = new ParameterDescription(object, p, f, m_bundle, this);
//          } else {
//            for (String name : p.names()) {
//              if (m_descriptions.containsKey(name)) {
//                throw new ParameterException("Found the option " + name + " multiple times");
//              }
//              p("Adding description for " + name);
//              ParameterDescription pd = new ParameterDescription(object, p, f, m_bundle, this);
//              m_fields.put(f, pd);
//              m_descriptions.put(name, pd);
//
//              if (p.required()) m_requiredFields.put(f, pd);
//            }
//          }
//        } else if (delegateAnnotation != null) {
//          //
//          // @ParametersDelegate
//          //
//          try {
//            Object delegateObject = f.get(object);
//            if (delegateObject == null){
//              throw new ParameterException("Delegate field '" + f.getName() + "' cannot be null.");
//            }
//            addDescription(delegateObject);
//          } catch (IllegalAccessException e) {
//          }
//        } else if (dynamicParameter != null) {
//          //
//          // @DynamicParameter
//          //
//          DynamicParameter dp = (DynamicParameter) dynamicParameter;
//          for (String name : dp.names()) {
//            if (m_descriptions.containsKey(name)) {
//              throw new ParameterException("Found the option " + name + " multiple times");
//            }
//            p("Adding description for " + name);
//            ParameterDescription pd = new ParameterDescription(object, dp, f, m_bundle, this);
//            m_fields.put(f, pd);
//            m_descriptions.put(name, pd);
//
//            if (dp.required()) m_requiredFields.put(f, pd);
//          }
//        }
//      }
//      // Traverse the super class until we find Object.class
//      cls = cls.getSuperclass();
//    }
  }

  private void initializeDefaultValue(ParameterDescription pd) {
    for (String optionName : pd.getParameter().names()) {
      String def = m_defaultProvider.getDefaultValueFor(optionName);
      if (def != null) {
        p("Initializing " + optionName + " with default value:" + def);
        pd.addValue(def, true /* default */);
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
    while (i < args.length && ! commandParsed) {
      String arg = args[i];
      String a = trim(arg);
      args[i] = a;
      p("Parsing arg: " + a);

      JCommander jc = findCommandByAlias(arg);
      int increment = 1;
      if (! isDashDash && ! "--".equals(a) && isOption(args, a) && jc == null) {
        //
        // Option
        //
        ParameterDescription pd = findParameterDescription(a);

        if (pd != null) {
          if (pd.getParameter().password()) {
            //
            // Password option, use the Console to retrieve the password
            //
            char[] password = readPassword(pd.getDescription(), pd.getParameter().echoInput());
            pd.addValue(new String(password));
            m_requiredFields.remove(pd.getParameterized());
          } else {
            if (pd.getParameter().variableArity()) {
              //
              // Variable arity?
              //
              increment = processVariableArity(args, i, pd);
            } else {
              //
              // Regular option
              //
              Class<?> fieldType = pd.getParameterized().getType();

              // Boolean, set to true as soon as we see it, unless it specified
              // an arity of 1, in which case we need to read the next value
              if ((fieldType == boolean.class || fieldType == Boolean.class)
                  && pd.getParameter().arity() == -1) {
                pd.addValue("true");
                m_requiredFields.remove(pd.getParameterized());
              } else {
                increment = processFixedArity(args, i, pd, fieldType);
              }
              // If it's a help option, remember for later
              if (pd.isHelp()) {
                m_helpWasSpecified = true;
              }
            }
          }
        } else {
          if (m_acceptUnknownOptions) {
            m_unknownArgs.add(arg);
            i++;
            while (i < args.length && ! isOption(args, args[i])) {
              m_unknownArgs.add(args[i++]);
            }
            increment = 0;
          } else {
            throw new ParameterException("Unknown option: " + arg);
          }
        }
      }
      else {
        //
        // Main parameter
        //
        if (! Strings.isStringEmpty(arg)) {
          if ("--".equals(arg)) {
              isDashDash = true;
              a = trim(args[++i]);
          }
          if (m_commands.isEmpty()) {
            //
            // Regular (non-command) parsing
            //
            List mp = getMainParameter(arg);
            String value = a; // If there's a non-quoted version, prefer that one
            Object convertedValue = value;

            if (m_mainParameter.getGenericType() instanceof ParameterizedType) {
              ParameterizedType p = (ParameterizedType) m_mainParameter.getGenericType();
              Type cls = p.getActualTypeArguments()[0];
              if (cls instanceof Class) {
                convertedValue = convertValue(m_mainParameter, (Class) cls, value);
              }
            }

            ParameterDescription.validateParameter(m_mainParameterDescription,
                m_mainParameterAnnotation.validateWith(),
                "Default", value);

            m_mainParameterDescription.setAssigned(true);
            mp.add(convertedValue);
          }
          else {
            //
            // Command parsing
            //
            if (jc == null && validate) {
                throw new MissingCommandException("Expected a command, got " + arg);
            } else if (jc != null){
                m_parsedCommand = jc.m_programName.m_name;
                m_parsedAlias = arg; //preserve the original form
    
                // Found a valid command, ask it to parse the remainder of the arguments.
                // Setting the boolean commandParsed to true will force the current
                // loop to end.
                jc.parse(subArray(args, i + 1));
                commandParsed = true;
            }
          }
        }
      }
      i += increment;
    }

    // Mark the parameter descriptions held in m_fields as assigned
    for (ParameterDescription parameterDescription : m_descriptions.values()) {
      if (parameterDescription.isAssigned()) {
        m_fields.get(parameterDescription.getParameterized()).setAssigned(true);
      }
    }

  }

  private class DefaultVariableArity implements IVariableArity {

    @Override
    public int processVariableArity(String optionName, String[] options) {
        int i = 0;
        while (i < options.length && !isOption(options, options[i])) {
          i++;
        }
        return i;
    }
  }
  private final IVariableArity DEFAULT_VARIABLE_ARITY = new DefaultVariableArity();

  private int m_verbose = 0;

  private boolean m_caseSensitiveOptions = true;
  private boolean m_allowAbbreviatedOptions = false;

  /**
   * @return the number of options that were processed.
   */
  private int processVariableArity(String[] args, int index, ParameterDescription pd) {
    Object arg = pd.getObject();
    IVariableArity va;
    if (! (arg instanceof IVariableArity)) {
        va = DEFAULT_VARIABLE_ARITY;
    } else {
        va = (IVariableArity) arg;
    }

    List<String> currentArgs = Lists.newArrayList();
    for (int j = index + 1; j < args.length; j++) {
      currentArgs.add(args[j]);
    }
    int arity = va.processVariableArity(pd.getParameter().names()[0],
        currentArgs.toArray(new String[0]));

    int result = processFixedArity(args, index, pd, List.class, arity);
    return result;
  }

  private int processFixedArity(String[] args, int index, ParameterDescription pd,
      Class<?> fieldType) {
    // Regular parameter, use the arity to tell use how many values
    // we need to consume
    int arity = pd.getParameter().arity();
    int n = (arity != -1 ? arity : 1);

    return processFixedArity(args, index, pd, fieldType, n);
  }

  private int processFixedArity(String[] args, int originalIndex, ParameterDescription pd,
                                Class<?> fieldType, int arity) {
    int index = originalIndex;
    String arg = args[index];
    // Special case for boolean parameters of arity 0
    if (arity == 0 &&
        (Boolean.class.isAssignableFrom(fieldType)
            || boolean.class.isAssignableFrom(fieldType))) {
      pd.addValue("true");
      m_requiredFields.remove(pd.getParameterized());
    } else if (index < args.length - 1) {
      int offset = "--".equals(args[index + 1]) ? 1 : 0;

      if (index + arity < args.length) {
        for (int j = 1; j <= arity; j++) {
          pd.addValue(trim(args[index + j + offset]));
          m_requiredFields.remove(pd.getParameterized());
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
    if (m_mainParameter == null) {
      throw new ParameterException(
          "Was passed main parameter '" + arg + "' but no main parameter was defined");
    }

    List<?> result = (List<?>) m_mainParameter.get(m_mainParameterObject);
    if (result == null) {
      result = Lists.newArrayList();
      if (! List.class.isAssignableFrom(m_mainParameter.getType())) {
        throw new ParameterException("Main parameter field " + m_mainParameter
            + " needs to be of type List, not " + m_mainParameter.getType());
      }
      m_mainParameter.set(m_mainParameterObject, result);
    }
    if (m_firstTimeMainParameter) {
      result.clear();
      m_firstTimeMainParameter = false;
    }
    return result;
  }

  public String getMainParameterDescription() {
    if (m_descriptions == null) createDescriptions();
    return m_mainParameterAnnotation != null ? m_mainParameterAnnotation.description()
        : null;
  }

//  private int longestName(Collection<?> objects) {
//    int result = 0;
//    for (Object o : objects) {
//      int l = o.toString().length();
//      if (l > result) result = l;
//    }
//
//    return result;
//  }

  /**
   * Set the program name (used only in the usage).
   */
  public void setProgramName(String name) {
    setProgramName(name, new String[0]);
  }

  /**
   * Set the program name
   *
   * @param name    program name
   * @param aliases aliases to the program name
   */
  public void setProgramName(String name, String... aliases) {
    m_programName = new ProgramName(name, Arrays.asList(aliases));
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
        bundle = m_bundle;
      }

      if (bundle != null) {
        result = getI18nString(bundle, p.commandDescriptionKey(), p.commandDescription());
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

  /**
   * Store the help in the passed string builder.
   */
  public void usage(StringBuilder out) {
    usage(out, "");
  }

  public void usage(StringBuilder out, String indent) {
    if (m_descriptions == null) createDescriptions();
    boolean hasCommands = !m_commands.isEmpty();

    //
    // First line of the usage
    //
    String programName = m_programName != null ? m_programName.getDisplayName() : "<main class>";
    out.append(indent).append("Usage: " + programName + " [options]");
    if (hasCommands) out.append(indent).append(" [command] [command options]");
    if (m_mainParameterDescription != null) {
      out.append(" " + m_mainParameterDescription.getDescription());
    }
    out.append("\n");

    //
    // Align the descriptions at the "longestName" column
    //
    int longestName = 0;
    List<ParameterDescription> sorted = Lists.newArrayList();
    for (ParameterDescription pd : m_fields.values()) {
      if (! pd.getParameter().hidden()) {
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
    int descriptionIndent = 6;
    if (sorted.size() > 0) out.append(indent).append("  Options:\n");
    for (ParameterDescription pd : sorted) {
      WrappedParameter parameter = pd.getParameter();
      out.append(indent).append("  "
          + (parameter.required() ? "* " : "  ")
          + pd.getNames()
          + "\n"
          + indent + s(descriptionIndent));
      int indentCount = indent.length() + descriptionIndent;
      wrapDescription(out, indentCount, pd.getDescription());
      Object def = pd.getDefault();
      if (pd.isDynamicParameter()) {
        out.append("\n" + s(indentCount + 1))
            .append("Syntax: " + parameter.names()[0]
                + "key" + parameter.getAssignment()
                + "value");
      }
      if (def != null) {
        String displayedDef = Strings.isStringEmpty(def.toString())
            ? "<empty string>"
            : def.toString();
        out.append("\n" + s(indentCount + 1))
            .append("Default: " + (parameter.password()?"********" : displayedDef));
      }
      Class<?> type =  pd.getParameterized().getType();
      if(type.isEnum()){
          out.append("\n" + s(indentCount + 1))
          .append("Possible Values: " + EnumSet.allOf((Class<? extends Enum>) type));
      }
      out.append("\n");
    }

    //
    // If commands were specified, show them as well
    //
    if (hasCommands) {
      out.append("  Commands:\n");
      // The magic value 3 is the number of spaces between the name of the option
      // and its description
      for (Map.Entry<ProgramName, JCommander> commands : m_commands.entrySet()) {
        Object arg = commands.getValue().getObjects().get(0);
        Parameters p = arg.getClass().getAnnotation(Parameters.class);
        if (!p.hidden()) {
          ProgramName progName = commands.getKey();
          String dispName = progName.getDisplayName();
          out.append(indent).append("    " + dispName); // + s(spaceCount) + getCommandDescription(progName.name) + "\n");

          // Options for this command
          usage(progName.getName(), out, "      ");
          out.append("\n");
        }
      }
    }
  }

  private Comparator<? super ParameterDescription> getParameterDescriptionComparator() {
    return m_parameterDescriptionComparator;
  }

  public void setParameterDescriptionComparator(Comparator<? super ParameterDescription> c) {
    m_parameterDescriptionComparator = c;
  }

  public void setColumnSize(int columnSize) {
    m_columnSize = columnSize;
  }

  public int getColumnSize() {
    return m_columnSize;
  }

  private void wrapDescription(StringBuilder out, int indent, String description) {
    int max = getColumnSize();
    String[] words = description.split(" ");
    int current = indent;
    int i = 0;
    while (i < words.length) {
      String word = words[i];
      if (word.length() > max || current + word.length() <= max) {
        out.append(" ").append(word);
        current += word.length() + 1;
      } else {
        out.append("\n").append(s(indent + 1)).append(word);
        current = indent;
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
    return new ArrayList<ParameterDescription>(m_fields.values());
  }

  /**
   * @return the main parameter description or null if none is defined.
   */
  public ParameterDescription getMainParameter() {
    return m_mainParameterDescription;
  }

  private void p(String string) {
    if (m_verbose > 0 || System.getProperty(JCommander.DEBUG_PROPERTY) != null) {
      getConsole().println("[JCommander] " + string);
    }
  }

  /**
   * Define the default provider for this instance.
   */
  public void setDefaultProvider(IDefaultProvider defaultProvider) {
    m_defaultProvider = defaultProvider;

    for (Map.Entry<ProgramName, JCommander> entry : m_commands.entrySet()) {
      entry.getValue().setDefaultProvider(defaultProvider);
    }
  }

  public void addConverterFactory(IStringConverterFactory converterFactory) {
    CONVERTER_FACTORIES.addFirst(converterFactory);
  }

  public <T> Class<? extends IStringConverter<T>> findConverter(Class<T> cls) {
    for (IStringConverterFactory f : CONVERTER_FACTORIES) {
      Class<? extends IStringConverter<T>> result = f.getConverter(cls);
      if (result != null) return result;
    }

    return null;
  }

  public Object convertValue(ParameterDescription pd, String value) {
    return convertValue(pd.getParameterized(), pd.getParameterized().getType(), value);
  }

  /**
   * @param type The type of the actual parameter
   * @param value The value to convert
   */
  public Object convertValue(Parameterized parameterized, Class type,
      String value) {
    Parameter annotation = parameterized.getParameter();

    // Do nothing if it's a @DynamicParameter
    if (annotation == null) return value;

    Class<? extends IStringConverter<?>> converterClass = annotation.converter();
    boolean listConverterWasSpecified = annotation.listConverter() != NoConverter.class;

    //
    // Try to find a converter on the annotation
    //
    if (converterClass == null || converterClass == NoConverter.class) {
      // If no converter specified and type is enum, used enum values to convert
      if (type.isEnum()){
        converterClass = type;
      } else {
        converterClass = findConverter(type);
      }
    }

    if (converterClass == null) {
      Type elementType = parameterized.findFieldGenericType();
      converterClass = elementType != null
          ? findConverter((Class<? extends IStringConverter<?>>) elementType)
          : StringConverter.class;
      // Check for enum type parameter
      if (converterClass == null && Enum.class.isAssignableFrom((Class) elementType)) {
        converterClass = (Class<? extends IStringConverter<?>>) elementType;
      }
    }

    IStringConverter<?> converter;
    Object result = null;
    try {
      String[] names = annotation.names();
      String optionName = names.length > 0 ? names[0] : "[Main class]";
      if (converterClass != null && converterClass.isEnum()) {
        try {
          result = Enum.valueOf((Class<? extends Enum>) converterClass, value);
        } catch (IllegalArgumentException e) {
            try {
                result = Enum.valueOf((Class<? extends Enum>) converterClass, value.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ParameterException("Invalid value for " + optionName + " parameter. Allowed values:" +
                        EnumSet.allOf((Class<? extends Enum>) converterClass));
            }
        } catch (Exception e) {
          throw new ParameterException("Invalid value for " + optionName + " parameter. Allowed values:" +
                      EnumSet.allOf((Class<? extends Enum>) converterClass));
        }
      } else {
        converter = instantiateConverter(optionName, converterClass);
        if (type.isAssignableFrom(List.class)
              && parameterized.getGenericType() instanceof ParameterizedType) {

          // The field is a List
          if (listConverterWasSpecified) {
            // If a list converter was specified, pass the value to it
            // for direct conversion
            IStringConverter<?> listConverter =
                instantiateConverter(optionName, annotation.listConverter());
            result = listConverter.convert(value);
          } else {
            // No list converter: use the single value converter and pass each
            // parsed value to it individually
            result = convertToList(value, converter, annotation.splitter());
          }
        } else {
          result = converter.convert(value);
        }
      }
    } catch (InstantiationException e) {
      throw new ParameterException(e);
    } catch (IllegalAccessException e) {
      throw new ParameterException(e);
    } catch (InvocationTargetException e) {
      throw new ParameterException(e);
    }

    return result;
  }

  /**
   * Use the splitter to split the value into multiple values and then convert
   * each of them individually.
   */
  private Object convertToList(String value, IStringConverter<?> converter,
      Class<? extends IParameterSplitter> splitterClass)
          throws InstantiationException, IllegalAccessException {
    IParameterSplitter splitter = splitterClass.newInstance();
    List<Object> result = Lists.newArrayList();
    for (String param : splitter.split(value)) {
      result.add(converter.convert(param));
    }
    return result;
  }

  private IStringConverter<?> instantiateConverter(String optionName,
      Class<? extends IStringConverter<?>> converterClass)
      throws IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    Constructor<IStringConverter<?>> ctor = null;
    Constructor<IStringConverter<?>> stringCtor = null;
    Constructor<IStringConverter<?>>[] ctors
        = (Constructor<IStringConverter<?>>[]) converterClass.getDeclaredConstructors();
    for (Constructor<IStringConverter<?>> c : ctors) {
      Class<?>[] types = c.getParameterTypes();
      if (types.length == 1 && types[0].equals(String.class)) {
        stringCtor = c;
      } else if (types.length == 0) {
        ctor = c;
      }
    }

    IStringConverter<?> result = stringCtor != null
        ? stringCtor.newInstance(optionName)
        : (ctor != null
            ? ctor.newInstance()
            : null);

    return result;
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
    JCommander jc = new JCommander(object);
    jc.setProgramName(name, aliases);
    jc.setDefaultProvider(m_defaultProvider);
    jc.setAcceptUnknownOptions(m_acceptUnknownOptions);
    ProgramName progName = jc.m_programName;
    m_commands.put(progName, jc);

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
                  + mappedName.m_name + " command");
        }
        aliasMap.put(alias, progName);
      }
    }
  }

  public Map<String, JCommander> getCommands() {
    Map<String, JCommander> res = Maps.newLinkedHashMap();
    for (Map.Entry<ProgramName, JCommander> entry : m_commands.entrySet()) {
      res.put(entry.getKey().m_name, entry.getValue());
    }
    return res;
  }

  public String getParsedCommand() {
    return m_parsedCommand;
  }

  /**
   * The name of the command or the alias in the form it was
   * passed to the command line. <code>null</code> if no
   * command or alias was specified.
   *
   * @return Name of command or alias passed to command line. If none passed: <code>null</code>.
   */
  public String getParsedAlias() {
    return m_parsedAlias;
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
    return m_objects;
  }

  private ParameterDescription findParameterDescription(String arg) {
    return FuzzyMap.findInMap(m_descriptions, new StringKey(arg), m_caseSensitiveOptions,
        m_allowAbbreviatedOptions);
  }

  private JCommander findCommand(ProgramName name) {
    return FuzzyMap.findInMap(m_commands, name,
        m_caseSensitiveOptions, m_allowAbbreviatedOptions);
//    if (! m_caseSensitiveOptions) {
//      return m_commands.get(name);
//    } else {
//      for (ProgramName c : m_commands.keySet()) {
//        if (c.getName().equalsIgnoreCase(name.getName())) {
//          return m_commands.get(c);
//        }
//      }
//    }
//    return null;
  }

  private ProgramName findProgramName(String name) {
    return FuzzyMap.findInMap(aliasMap, new StringKey(name),
        m_caseSensitiveOptions, m_allowAbbreviatedOptions);
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
    private final String m_name;
    private final List<String> m_aliases;

    ProgramName(String name, List<String> aliases) {
      m_name = name;
      m_aliases = aliases;
    }

    @Override
    public String getName() {
      return m_name;
    }

    private String getDisplayName() {
      StringBuilder sb = new StringBuilder();
      sb.append(m_name);
      if (!m_aliases.isEmpty()) {
        sb.append("(");
        Iterator<String> aliasesIt = m_aliases.iterator();
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
      result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
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
      if (m_name == null) {
        if (other.m_name != null)
          return false;
      } else if (!m_name.equals(other.m_name))
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
    m_verbose = verbose;
  }

  public void setCaseSensitiveOptions(boolean b) {
    m_caseSensitiveOptions = b;
  }

  public void setAllowAbbreviatedOptions(boolean b) {
    m_allowAbbreviatedOptions = b;
  }

  public void setAcceptUnknownOptions(boolean b) {
    m_acceptUnknownOptions = b;
  }

  public List<String> getUnknownOptions() {
    return m_unknownArgs;
  }
  public void setAllowParameterOverwriting(boolean b) {
    m_allowParameterOverwriting = b;
  }

  public boolean isParameterOverwritingAllowed() {
    return m_allowParameterOverwriting;
  }
//  public void setCaseSensitiveCommands(boolean b) {
//    m_caseSensitiveCommands = b;
//  }
}

