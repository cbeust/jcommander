package com.beust.jcommander.parameterized.parser;

import com.beust.jcommander.IParameterizedParser;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameterized;
import com.beust.jcommander.ParametersDelegate;
import com.beust.jcommander.WrappedParameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.converters.NoConverter;
import com.beust.jcommander.internal.Sets;
import com.beust.jcommander.validators.NoValidator;
import com.beust.jcommander.validators.NoValueValidator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sun.reflect.annotation.AnnotationParser;

/**
 * Provides building JCommander Parameters based on @ComponentInput and @ComponentConfiguration as
 * opposed to using JCommander @Parameter.
 *
 * @author Cedric Beust <cedric@beust.com>
 * @author Tim Gallagher
 */
public class JsonAnnotationParameterizedParser implements IParameterizedParser {

  public static final String PREFIX_MARKER = "prefix:";

  /**
   * This is the standard prefix like "--" or "-"
   */
  protected final String paramPrefix;

  /**
   * When a class has a member class and is annotated with JsonProperty, then there couple be fields
   * that are the same name, for example 'version'. This map allows the parser to define a Parameter
   * with a prefix in order to avoid the collision.
   */
  protected final Map<Class, String> classPrefixes = new HashMap<>();

  /**
   * This used in auto generation of the prefixes from the JsonDescription. If this, prefix
   * separator value is not in the descriptions prefix definition, then it will be added. The
   * default is '.'
   */
  protected String prefixSeparator = ".";

  public JsonAnnotationParameterizedParser() {
    this("");
  }

  public JsonAnnotationParameterizedParser(String paramPrefix) {
    this.paramPrefix = paramPrefix;
  }

  public void addClassPrefix(Class clazz, String prefix) {
    this.classPrefixes.put(clazz, prefix);
  }

  public void setPrefixSeparator(String separator) {
    this.prefixSeparator = separator == null ? "" : separator;
  }

  /**
   * Recursive handler for describing the set of classes while using the setOfClasses parameter as a
   * collector
   *
   * @param inputClass the class to analyze
   * @param setOfClasses the set collector to collect the results
   */
  private void describeClassTree(Class<?> inputClass, Set<Class<?>> setOfClasses) {
    // can't map null class
    if (inputClass == null) {
      return;
    }

    // don't further analyze a class that has been analyzed already
    if (Object.class.equals(inputClass) || setOfClasses.contains(inputClass)) {
      return;
    }

    // add to analysis set
    setOfClasses.add(inputClass);

    // perform super class analysis
    describeClassTree(inputClass.getSuperclass(), setOfClasses);

    // perform analysis on interfaces
    for (Class<?> hasInterface : inputClass.getInterfaces()) {
      describeClassTree(hasInterface, setOfClasses);
    }
  }

  /**
   * Given an object return the set of classes that it extends or implements.
   *
   * @param arg object to describe
   * @return set of classes that are implemented or extended by that object
   */
  private Set<Class<?>> describeClassTree(Class<?> inputClass) {
    if (inputClass == null) {
      return Collections.emptySet();
    }

    // create result collector
    Set<Class<?>> classes = Sets.newLinkedHashSet();

    // describe tree
    describeClassTree(inputClass, classes);

    return classes;
  }

  @Override
  public List<Parameterized> parseArg(Object arg) {
    List<Parameterized> result = Parameterized.parseArg(arg);

    Class<?> rootClass = arg.getClass();

    // get the list of types that are extended or implemented by the root class
    // and all of its parent types
    Set<Class<?>> types = describeClassTree(rootClass);

    // analyze each type
    for (Class<?> curClazz : types) {
      // check fields
      for (Field field : curClazz.getDeclaredFields()) {
        JsonProperty fieldAnnotation = (JsonProperty) field.getAnnotation(JsonProperty.class);
        JsonPropertyDescription descrAnnotation = (JsonPropertyDescription) field.getAnnotation(JsonPropertyDescription.class);
        MyDelegate myDelegate = (MyDelegate) field.getAnnotation(MyDelegate.class);
        if (fieldAnnotation != null) {         
          // this is a map of annotation field names uses to create the Parameter annotation 
          // at runtime
          Map<String, Object> map = new HashMap<>();

          /*
          * For primitive and their derived types, we can use the Parameter annotation, but for
          * other user classes, we need to add a delegate
           */
          if (isPrimitiveOrString(field) || myDelegate == null) {
            /*
            * create standard Parameter
             */
            String name = fieldAnnotation.value();
            map.put("names", new String[]{name});
            map.put("required", fieldAnnotation.required());
            map.put("descriptionKey", "");
            // all variable types, even Boolean require 1 following parameter.
            //if (field.getType() == Boolean.class || field.getType() == boolean.class) {
            map.put("arity", 1);
            map.put("variableArity", (field.getType() == List.class));
            map.put("password", false);
            map.put("converter", NoConverter.class);
            map.put("listConverter", NoConverter.class);
            map.put("hidden", false);
            map.put("validateWith", new Class[]{NoValidator.class});
            map.put("validateValueWith", new Class[]{NoValueValidator.class});
            map.put("splitter", CommaParameterSplitter.class);
            map.put("echoInput", true);
            map.put("help", false);
            map.put("forceNonOverwritable", false);
            map.put("order", -1);
            map.put("description", descrAnnotation != null ? descrAnnotation.value() : "");

            Parameter param = (Parameter) AnnotationParser.annotationForMap(Parameter.class, map);
            result.add(new Parameterized(new WrappedParameter(param), null, field, null));
          } else {
            /*
            * Create ParametersDelegate
             */
            ParametersDelegate param = (ParametersDelegate) AnnotationParser.annotationForMap(ParametersDelegate.class, map);
            result.add(new Parameterized(null, param, field, null));
          }
        }
      }

      /*
      * This section would be for completeness and although it is not tested it is left here
      * as a template to use the JsonSetter (or JsonGetter) methods as ways to define parameters
      * at runtime.
      */
//      // check methods
//      for (Method method : curClazz.getDeclaredMethods()) {
//        // these only work on setMethods
//        if (!method.getName().startsWith("set")) {
//          continue;
//        }
//
//        JsonSetter jsonSetterAnnotation = (JsonSetter) method.getAnnotation(JsonSetter.class);
//        if (jsonSetterAnnotation != null) {
//          Map<String, Object> map = new HashMap<String, Object>();
//
//          /*
//          * For primitive and their derived types, we can use the Parameter annotation, but for
//          * other user classes, we need to add a delegate
//           */
// /*
//            * create standard Parameter
//           */
//          String name = jsonSetterAnnotation.value();
//          map.put("names", new String[]{name});
//          map.put("required", false);
//          //
//          // TODO SET THE DEFAULT VALUE BASED ON THE values() OR valuesEnum()
//          // map.put("default", annotation.defaultValue());
//          //
//          map.put("descriptionKey", "");
//          // get the parameter type
//          Class[] paramTypes = method.getParameterTypes();
//          // there should only be one for a 
//          // all variable types, even Boolean require 1 following parameter.
//          //if (paramTypes[0] == Boolean.class || paramTypes[0] == boolean.class) {
//          map.put("arity", 1);
//          //} 
//          map.put("variableArity", (paramTypes[0] == List.class));
//          map.put("password", false);
//          map.put("converter", NoConverter.class);
//          map.put("listConverter", NoConverter.class);
//          map.put("hidden", false);
//          map.put("validateWith", new Class[]{NoValidator.class});
//          map.put("validateValueWith", new Class[]{NoValueValidator.class});
//          map.put("splitter", CommaParameterSplitter.class);
//          map.put("echoInput", true);
//          map.put("help", false);
//          map.put("forceNonOverwritable", false);
//          map.put("order", -1);
//          map.put("description", "");
//
//          Parameter param = (Parameter) AnnotationParser.annotationForMap(Parameter.class, map);
//          result.add(new Parameterized(new WrappedParameter(param), null, null, method));
//        }
//      }
    }

    return result;
  }

  /**
   * Basic check for primitive or Java class that should be used directly.
   * 
   * @param field non-null java Field
   * @return true if Java primitive or part of the Java or Sun package.
   */
  public boolean isPrimitiveOrString(Field field) {
    Class type = field.getType();
    String name = type.getName();

    return type.isPrimitive() || name.startsWith("java") || name.startsWith("sun");
  }

}
