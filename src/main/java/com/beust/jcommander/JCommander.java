package com.beust.jcommander;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The main class for JCommander. It's responsible for parsing the object that contains
 * all the annotated fields, parse the command line and assign the fields with the correct
 * values and a few other helper methods, such as usage().
 * 
 * The object(s) you pass in the constructor are expected to have one or more
 * @Parameter annotations on them. You can pass either a single object, an array of objects 
 * or an instance of Iterable. In the case of an array or Iterable, JCommander will collect
 * the @Parameter annotations from all the objects passed in parameter.
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
  private List<Object> m_objects;

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

  public JCommander(Object object) {
    init(object, null);
  }

  public JCommander(Object object, ResourceBundle bundle, String... args) {
    init(object, bundle);
    parse(args);
  }

  public JCommander(Object object, String... args) {
    init(object, null);
    parse(args);
  }

  private void init(Object object, ResourceBundle bundle) {
    m_bundle = bundle;
    m_objects = Lists.newArrayList();
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

    createDescriptions();
  }

  /**
   * Parse the command line parameters.
   */
  public void parse(String... args) {
    parseValues(expandArgs(args));
    validateOptions();
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
    
  }
  
  /**
   * Expand the command line parameters to take @ parameters into account.
   * When @ is encountered, the content of the file that follows is inserted
   * in the command line.
   * 
   * @param originalArgv the original command line parameters
   * @return the new and enriched command line parameters
   */
  private static String[] expandArgs(String[] originalArgv) {
    List<String> vResult = Lists.newArrayList();
    
    for (String arg : originalArgv) {

      if (arg.startsWith("@")) {
        String fileName = arg.substring(1);
        vResult.addAll(readFile(fileName));
      }
      else {
        vResult.add(arg);
      }
    }
    
    return vResult.toArray(new String[vResult.size()]);
  }

  /**
   * Reads the file specified by filename and returns the file content as a string.
   * End of lines are replaced by a space.
   * 
   * @param fileName the command line filename
   * @return the file content as a string.
   */
  public static List<String> readFile(String fileName) {
    List<String> result = Lists.newArrayList();

    try {
      BufferedReader bufRead = new BufferedReader(new FileReader(fileName));

      String line;

      // Read through file one line at time. Print line # and line
      while ((line = bufRead.readLine()) != null) {
        result.add(line);
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
   * Create the ParameterDescriptions for all the @Parameter found.
   */
  private void createDescriptions() {
    m_descriptions = Maps.newHashMap();

    for (Object object : m_objects) {
      Class<?> cls = object.getClass();
      for (Field f : cls.getDeclaredFields()) {
        p("Field:" + f.getName());
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
          } else {
            for (String name : p.names()) {
              if (m_descriptions.containsKey(name)) {
                throw new ParameterException("Found the option " + name + " multiple times");
              }
              p("Adding description for " + name);
              ParameterDescription pd = new ParameterDescription(object, p, f, m_bundle);
              m_fields.put(f, pd);
              m_descriptions.put(name, pd);
              if (p.required()) m_requiredFields.put(f, pd);
            }
          }
        }
      }
    }
  }

  /**
   * Main method that parses the values and initializes the fields accordingly.
   */
  private void parseValues(String[] args) {
    for (int i = 0; i < args.length; i++) {
      String a = trim(args[i]);
      p("Parsing arg:" + a);
      if (a.startsWith("-")) {
        ParameterDescription pd = m_descriptions.get(a);
        if (pd != null) {
          if (pd.getParameter().password()) {
            //
            // Password option, use the Console to retrieve the password
            //
            Console console = System.console();
            if (console == null) {
              throw new ParameterException("No console is available to get parameter " + a);
            }
            System.out.print("Value for " + a + " (" + pd.getDescription() + "):");
            char[] password = console.readPassword();
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
              if (i + n < args.length) {
                for (int j = 1; j <= n; j++) {
                  pd.addValue(trim(args[i + j]));
                  m_requiredFields.remove(pd.getField());
                }
                i += n;
              } else {
                throw new ParameterException(n + " parameters expected after " + args[i]);
              }
            }
          }
        } else {
          throw new ParameterException("Unknown option: " + a);
        }
      }
      else {
        if (! isStringEmpty(args[i])) getMainParameter(args[i]).add(args[i]);
      }
    }
  }

  private static boolean isStringEmpty(String s) {
    return s == null || "".equals(s);
  }

  /**
   * @return the field that's meant to receive all the parameters that are not options.
   * 
   * @param arg the arg that we're about to add (only passed here to ouput a meaningful
   * error message).
   */
  private List<String> getMainParameter(String arg) {
    if (m_mainParameterField == null) {
      throw new ParameterException(
          "Was passed main parameter '" + arg + "' but no main parameter was defined");
    }

    try {
      @SuppressWarnings("unchecked")
      List<String> result = (List<String>) m_mainParameterField.get(m_mainParameterObject);
      if (result == null) {
        result = Lists.newArrayList();
        m_mainParameterField.set(m_mainParameterObject, result);
      }
      return result;
    }
    catch(IllegalAccessException ex) {
      throw new ParameterException("Couldn't access main parameter: " + ex.getMessage());
    }
  }

  /**
   * Display a the help on System.out.
   */
  public void usage() {
    StringBuilder sb = new StringBuilder("Usage: <main class> [options]");
    if (m_mainParameterAnnotation != null) {
      sb.append(" " + m_mainParameterAnnotation.description());
    }
    sb.append("\n  Options:");
    System.out.println(sb.toString());

    // Will contain the size of the longest option name
    int longestName = 0;
    List<ParameterDescription> sorted = Lists.newArrayList();
    for (ParameterDescription pd : m_fields.values()) {
      if (! pd.getParameter().hidden()) {
        sorted.add(pd);
        // +1 to have an extra space between the name and the description
        int length = pd.getNames().length() + 1;
        if (length > longestName) {
          longestName = length;
        }
      }
    }

    // Calculate the tab stop at which all the descriptions should be
    // aligned based on the longest option name found.
    int target = longestName %8 != 0 ? (((longestName + 8) / 8) * 8): longestName;
    Collections.sort(sorted, new Comparator<ParameterDescription>() {
      @Override
      public int compare(ParameterDescription arg0, ParameterDescription arg1) {
        return arg0.getNames().compareTo(arg1.getNames());
      }
    });

    // Display all the names and descriptions at the right tab position
    for (ParameterDescription pd : sorted) {
      int l = target - pd.getNames().length();
      int tabCount = l / 8 + (l % 8 == 0 ? 0 : 1);
      StringBuilder tabs = new StringBuilder();
      for (int i = 0; i < tabCount; i++) tabs.append("\t");
      System.out.println("    " + pd.getNames() + tabs + pd.getDescription());
    }
  }

  /**
   * @return a Collection of all the @Parameter annotations found on the
   * target class. This can be used to display the usage() in a different
   * format (e.g. HTML).
   */
  public List<ParameterDescription> getParameters() {
    return new ArrayList<ParameterDescription>(m_fields.values());
  }

  private void p(String string) {
    if (System.getProperty(JCommander.DEBUG_PROPERTY) != null) {
      System.out.println("[JCommander] " + string);
    }
  }
}

