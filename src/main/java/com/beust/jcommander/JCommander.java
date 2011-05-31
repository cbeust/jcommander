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

import com.beust.jcommander.converters.NoConverter;
import com.beust.jcommander.converters.StringConverter;
import com.beust.jcommander.internal.DefaultConverterFactory;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;



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
 * @author cbeust
 */
public class JCommander {
  public static final String DEBUG_PROPERTY = "jcommander.debug";

  /**
   * A map to look up parameter description per option name.
   */
  private Map<String, ParameterDescription> m_descriptions;

  /**
   * The objects that contain fields annotated with @Parameter.
   */
  private List<Object> m_objects = Lists.newArrayList();

  /**
   * This field will contain whatever command line parameter is not an option.
   * It is expected to be a List<String>.
   */
  private Field m_mainParameterField = null;

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
   * A set of all the fields that are required. During the reflection phase,
   * this field receives all the fields that are annotated with required=true
   * and during the parsing phase, all the fields that are assigned a value
   * are removed from it. At the end of the parsing phase, if it's not empty,
   * then some required fields did not receive a value and an exception is
   * thrown.
   */
  private Map<Field, ParameterDescription> m_requiredFields = Maps.newHashMap();

  /**
   * A map of all the annotated fields.
   */
  private Map<Field, ParameterDescription> m_fields = Maps.newHashMap();

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
  private Map<String, ProgramName> aliasMap = Maps.newLinkedHashMap();

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
  public JCommander(Object object, ResourceBundle bundle) {
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
    parseValues(expandArgs(args));
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
    }
  }

  /**
   * Make sure that all the required parameters have received a value.
   */
  private void validateOptions() {
    if (! m_requiredFields.isEmpty()) {
      StringBuilder missingFields = new StringBuilder();
      for (ParameterDescription pd : m_requiredFields.values()) {
        missingFields.append(pd.getNames()).append(" ");
      }
      throw new ParameterException("The following options are required: " + missingFields);
    }

    if (m_mainParameterDescription != null) {
      if (m_mainParameterDescription.getParameter().required() &&
          !m_mainParameterDescription.isAssigned()) {
        throw new ParameterException("Main parameters are required (\""
            + m_mainParameterDescription.getDescription() + "\")");
      }
    }
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
        vResult1.add(arg);
      }
    }

    //
    // Expand separators
    //
    List<String> vResult2 = Lists.newArrayList();
    for (int i = 0; i < vResult1.size(); i++) {
      String arg = vResult1.get(i);
      String[] v1 = vResult1.toArray(new String[0]);
      if (isOption(v1, arg)) {
        String sep = getSeparatorFor(v1, arg);
        if (! " ".equals(sep)) {
          String[] sp = arg.split("[" + sep + "]");
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

  private boolean isOption(String[] args, String arg) {
    String prefixes = getOptionPrefixes(args, arg);
    return prefixes.indexOf(arg.charAt(0)) >= 0;
  }

  private ParameterDescription getPrefixDescriptionFor(String arg) {
    for (Map.Entry<String, ParameterDescription> es : m_descriptions.entrySet()) {
      if (arg.startsWith(es.getKey())) return es.getValue();
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

    return Parameters.DEFAULT_OPTION_PREFIXES;
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
        // Allow empty lines in these at files
        if (line.length() > 0) result.add(line);
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
    if (result.startsWith("\"")) {
      if (result.endsWith("\"")) {
          return result.substring(1, result.length() - 1);
      }
      return result.substring(1);
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

    while (!Object.class.equals(cls)) {
      for (Field f : cls.getDeclaredFields()) {
        p("Field:" + cls.getSimpleName() + "." + f.getName());
        f.setAccessible(true);
        Annotation annotation = f.getAnnotation(Parameter.class);
        if (annotation != null) {
          Parameter p = (Parameter) annotation;
          if (p.names().length == 0) {
            p("Found main parameter:" + f);
            if (m_mainParameterField != null) {
              throw new ParameterException("Only one @Parameter with no names attribute is"
                  + " allowed, found:" + m_mainParameterField + " and " + f);
            }
            m_mainParameterField = f;
            m_mainParameterObject = object;
            m_mainParameterAnnotation = p;
            m_mainParameterDescription = new ParameterDescription(object, p, f, m_bundle, this);
          } else {
            for (String name : p.names()) {
              if (m_descriptions.containsKey(name)) {
                throw new ParameterException("Found the option " + name + " multiple times");
              }
              p("Adding description for " + name);
              ParameterDescription pd = new ParameterDescription(object, p, f, m_bundle, this);
              m_fields.put(f, pd);
              m_descriptions.put(name, pd);

              if (p.required()) m_requiredFields.put(f, pd);
            }
          }
        }
      }
      // Traverse the super class until we find Object.class
      cls = cls.getSuperclass();
    }
  }

  private void initializeDefaultValue(ParameterDescription pd) {
    String optionName = pd.getParameter().names()[0];
    String def = m_defaultProvider.getDefaultValueFor(optionName);
    if (def != null) {
      p("Initializing " + optionName + " with default value:" + def);
      pd.addValue(def, true /* default */);
    }
  }

  /**
   * Main method that parses the values and initializes the fields accordingly.
   */
  private void parseValues(String[] args) {
    // This boolean becomes true if we encounter a command, which indicates we need
    // to stop parsing (the parsing of the command will be done in a sub JCommander
    // object)
    boolean commandParsed = false;
    int i = 0;
    while (i < args.length && ! commandParsed) {
      String arg = args[i];
      String a = trim(arg);
      p("Parsing arg:" + a);

      if (isOption(args, a)) {
        //
        // Option
        //
        ParameterDescription pd = m_descriptions.get(a);

        if (pd != null) {
          if (pd.getParameter().password()) {
            //
            // Password option, use the Console to retrieve the password
            //
            char[] password = readPassword(pd.getDescription());
            pd.addValue(new String(password));
          } else {
            //
            // Regular option
            //
            Class<?> fieldType = pd.getField().getType();

            // Boolean, set to true as soon as we see it, unless it specified
            // an arity of 1, in which case we need to read the next value
            if ((fieldType == boolean.class || fieldType == Boolean.class)
                && pd.getParameter().arity() == -1) {
              pd.addValue("true");
              m_requiredFields.remove(pd.getField());
            } else {
              // Regular parameter, use the arity to tell use how many values
              // we need to consume
              int arity = pd.getParameter().arity();
              int n = (arity != -1 ? arity : 1);

              // Special case for boolean parameters of arity 0
              if (n == 0 &&
                  (Boolean.class.isAssignableFrom(fieldType)
                      || boolean.class.isAssignableFrom(fieldType))) {
                pd.addValue("true");
                m_requiredFields.remove(pd.getField());
              } else if (i < args.length - 1) {
                int offset = "--".equals(args[i + 1]) ? 1 : 0;

                if (i + n < args.length) {
                  for (int j = 1; j <= n; j++) {
                    pd.addValue(trim(args[i + j + offset]));
                    m_requiredFields.remove(pd.getField());
                  }
                  i += n + offset;
                } else {
                  throw new ParameterException("Expected " + n + " values after " + arg);
                }
              } else {
                throw new ParameterException("Expected a value after parameter " + arg);
              }
            }
          }
        } else {
          throw new ParameterException("Unknown option: " + a);
        }
      }
      else {
        //
        // Main parameter
        //
        if (! isStringEmpty(arg)) {
          if (m_commands.isEmpty()) {
            //
            // Regular (non-command) parsing
            //
            List mp = getMainParameter(arg);
            String value = arg;
            Object convertedValue = value;

            if (m_mainParameterField.getGenericType() instanceof ParameterizedType) {
              ParameterizedType p = (ParameterizedType) m_mainParameterField.getGenericType();
              Type cls = p.getActualTypeArguments()[0];
              if (cls instanceof Class) {
                convertedValue = convertValue(m_mainParameterField, (Class) cls, value);
              }
            }

            m_mainParameterDescription.setAssigned(true);
            mp.add(convertedValue);
          }
          else {
            //
            // Command parsing
            //
            JCommander jc = findCommandByAlias(arg);
            if (jc == null) throw new MissingCommandException("Expected a command, got " + arg);
            m_parsedCommand = jc.m_programName.name;
            m_parsedAlias = arg; //preserve the original form

            // Found a valid command, ask it to parse the remainder of the arguments.
            // Setting the boolean commandParsed to true will force the current
            // loop to end.
            jc.parse(subArray(args, i + 1));
            commandParsed = true;
          }
        }
      }
      i++;
    }

    // Mark the parameter descriptions held in m_fields as assigned
    for (ParameterDescription parameterDescription : m_descriptions.values()) {
      if (parameterDescription.isAssigned()) {
        m_fields.get(parameterDescription.getField()).setAssigned(true);
      }
    }

  }

  /**
   * Invoke Console.readPassword through reflection to avoid depending
   * on Java 6.
   */
  private char[] readPassword(String description) {
    System.out.print(description + ": ");
    try {
      Method consoleMethod = System.class.getDeclaredMethod("console", new Class<?>[0]);
      Object console = consoleMethod.invoke(null, new Object[0]);
      Method readPassword = console.getClass().getDeclaredMethod("readPassword", new Class<?>[0]);
      return (char[]) readPassword.invoke(console, new Object[0]);
    } catch (Throwable t) {
      return readLine(description);
    }
  }

  /**
   * Read a line from stdin (used when java.io.Console is not available)
   */
  private char[] readLine(String description) {
    try {
      InputStreamReader isr = new InputStreamReader(System.in);
      BufferedReader in = new BufferedReader(isr);
      String result = in.readLine();
      in.close();
      isr.close();
      return result.toCharArray();
    } catch (IOException e) {
      throw new ParameterException(e);
    }
  }

  private String[] subArray(String[] args, int index) {
    int l = args.length - index;
    String[] result = new String[l];
    System.arraycopy(args, index, result, 0, l);

    return result;
  }

  private static boolean isStringEmpty(String s) {
    return s == null || "".equals(s);
  }

  /**
   * @return the field that's meant to receive all the parameters that are not options.
   *
   * @param arg the arg that we're about to add (only passed here to output a meaningful
   * error message).
   */
  private List<?> getMainParameter(String arg) {
    if (m_mainParameterField == null) {
      throw new ParameterException(
          "Was passed main parameter '" + arg + "' but no main parameter was defined");
    }

    try {
      List result = (List) m_mainParameterField.get(m_mainParameterObject);
      if (result == null) {
        result = Lists.newArrayList();
        if (! List.class.isAssignableFrom(m_mainParameterField.getType())) {
          throw new ParameterException("Main parameter field " + m_mainParameterField
              + " needs to be of type List, not " + m_mainParameterField.getType());
        }
        m_mainParameterField.set(m_mainParameterObject, result);
      }
      return result;
    }
    catch(IllegalAccessException ex) {
      throw new ParameterException("Couldn't access main parameter: " + ex.getMessage());
    }
  }

  public String getMainParameterDescription() {
    if (m_descriptions == null) createDescriptions();
    return m_mainParameterAnnotation != null ? m_mainParameterAnnotation.description()
        : null;
  }

  private int longestName(Collection<?> objects) {
    int result = 0;
    for (Object o : objects) {
      int l = o.toString().length();
      if (l > result) result = l;
    }

    return result;
  }

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
    System.out.println(sb.toString());
  }

  /**
   * Store the help for the command in the passed string builder.
   */
  public void usage(String commandName, StringBuilder out) {
    String description = getCommandDescription(commandName);
    JCommander jc = findCommandByAlias(commandName);
    if (description != null) {
      out.append(description);
      out.append("\n");
    }
    jc.usage(out);
  }

  /**
   * @return the description of the command.
   */
  public String getCommandDescription(String commandName) {
    JCommander jc = findCommandByAlias(commandName);
    if (jc == null) {
      throw new ParameterException("Asking description for unknown command: " + commandName);
    }

    Parameters p = jc.getObjects().get(0).getClass().getAnnotation(Parameters.class);
    String result = jc.getMainParameterDescription();
    if (p != null) result = p.commandDescription();

    return result;
  }

  /**
   * Display a the help on System.out.
   */
  public void usage() {
    StringBuilder sb = new StringBuilder();
    usage(sb);
    System.out.println(sb.toString());
  }

  /**
   * Store the help in the passed string builder.
   */
  public void usage(StringBuilder out) {
    if (m_descriptions == null) createDescriptions();
    boolean hasCommands = !m_commands.isEmpty();

    //
    // First line of the usage
    //
    String programName = m_programName != null ? m_programName.getDisplayName() : "<main class>";
    out.append("Usage: " + programName + " [options]");
    if (hasCommands) out.append(" [command] [command options]");
    out.append("\n");
    if (m_mainParameterAnnotation != null) {
      out.append(" " + m_mainParameterAnnotation.description() + "\n");
    }

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
    Collections.sort(sorted, new Comparator<ParameterDescription>() {
      public int compare(ParameterDescription p0, ParameterDescription p1) {
        return p0.getLongestName().compareTo(p1.getLongestName());
      }
    });

    //
    // Display all the names and descriptions
    //
    if (sorted.size() > 0) out.append("  Options:\n");
    for (ParameterDescription pd : sorted) {
      int l = pd.getNames().length();
      int spaceCount = longestName - l;
      int start = out.length();
      out.append("  "
          + (pd.getParameter().required() ? "* " : "  ")
          + pd.getNames() + s(spaceCount));
      int indent = out.length() - start;
      wrapDescription(out, indent, pd.getDescription());
      Object def = pd.getDefault();
      if (def != null) out.append("\n" + spaces(indent + 1)).append("Default: " + def);
      out.append("\n");
    }

    //
    // If commands were specified, show them as well
    //
    if (hasCommands) {
      out.append("  Commands:\n");
      // The magic value 3 is the number of spaces between the name of the option
      // and its description
      int ln = longestName(m_commands.keySet()) + 3;
      for (Map.Entry<ProgramName, JCommander> commands : m_commands.entrySet()) {
        ProgramName progName = commands.getKey();
        String dispName = progName.getDisplayName();
        int spaceCount  = ln - dispName.length();
        out.append("    " + dispName + s(spaceCount) + getCommandDescription(progName.name) + "\n");
      }
    }
  }

  private void wrapDescription(StringBuilder out, int indent, String description) {
    int max = 79;
    String[] words = description.split(" ");
    int current = indent;
    int i = 0;
    while (i < words.length) {
      String word = words[i];
      if (word.length() > max || current + word.length() <= max) {
        out.append(" ").append(word);
        current += word.length() + 1;
      } else {
        out.append("\n").append(spaces(indent + 1)).append(word);
        current = indent;
      }
      i++;
    }
  }

  private String spaces(int indent) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indent; i++) sb.append(" ");
    return sb.toString();
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
    if (System.getProperty(JCommander.DEBUG_PROPERTY) != null) {
      System.out.println("[JCommander] " + string);
    }
  }

  /**
   * Define the default provider for this instance.
   */
  public void setDefaultProvider(IDefaultProvider defaultProvider) {
    m_defaultProvider = defaultProvider;
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
    return convertValue(pd.getField(), pd.getField().getType(), value);
  }

  /**
   * @param field The field
   * @param type The type of the actual parameter
   * @param value The value to convert
   */
  public Object convertValue(Field field, Class type, String value) {
    Parameter annotation = field.getAnnotation(Parameter.class);
    Class<? extends IStringConverter<?>> converterClass = annotation.converter();

    //
    // Try to find a converter on the annotation
    //
    if (converterClass == null || converterClass == NoConverter.class) {
      converterClass = findConverter(type);
    }
    if (converterClass == null) {
      converterClass = StringConverter.class;
    }

    //
//    //
//    // Try to find a converter in the factory
//    //
//    IStringConverter<?> converter = null;
//    if (converterClass == null && m_converterFactories != null) {
//      // Mmmh, javac requires a cast here
//      converter = (IStringConverter) m_converterFactories.getConverter(type);
//    }

//    if (converterClass == null) {
//      throw new ParameterException("Don't know how to convert " + value
//          + " to type " + type + " (field: " + field.getName() + ")");
//    }

    IStringConverter<?> converter;
    Object result = null;
    try {
      String[] names = annotation.names();
      String optionName = names.length > 0 ? names[0] : "[Main class]";
      converter = instantiateConverter(optionName, converterClass);
      result = converter.convert(value);
    } catch (InstantiationException e) {
      throw new ParameterException(e);
    } catch (IllegalAccessException e) {
      throw new ParameterException(e);
    } catch (InvocationTargetException e) {
      throw new ParameterException(e);
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
        : ctor.newInstance();

        return result;
  }

  /**
   * Add a command object.
   */
  public void addCommand(String name, Object object) {
    addCommand(name, object, new String[0]);
  }

  /**
   * Add a command object.
   */
  public void addCommand(String name, Object object, String... aliases) {
    JCommander jc = new JCommander(object);
    jc.setProgramName(name, aliases);
    ProgramName progName = jc.m_programName;
    m_commands.put(progName, jc);

    /*
    * Register aliases
    */
    //register command name as an alias of itself for reverse lookup
    //Note: Name clash check is intentionally omitted to resemble the
    //     original behaviour of clashing commands.
    //     Aliases are, however, are strictly checked for name clashes.
    aliasMap.put(name, progName);
    for (String alias : aliases) {
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
    for (Map.Entry<ProgramName, JCommander> entry : m_commands.entrySet()) {
      res.put(entry.getKey().name, entry.getValue());
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

  /*
  * Reverse lookup JCommand object by command's name or its alias
  */
  private JCommander findCommandByAlias(String commandOrAlias) {
    ProgramName progName = aliasMap.get(commandOrAlias);
    if (progName == null) {
      return null;
    }
    JCommander jc = m_commands.get(progName);
    if (jc == null) {
      throw new IllegalStateException(
              "There appears to be inconsistency in the internal command database. " +
                      " This is likely a bug. Please report.");
    }
    return jc;
  }

  private static final class ProgramName {
    private final String name;
    private final List<String> aliases;

    ProgramName(String name) {
      this(name, Collections.<String>emptyList());
    }

    ProgramName(String name, List<String> aliases) {
      this.name = name;
      this.aliases = aliases;
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
}
