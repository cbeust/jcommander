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

import com.beust.jcommander.validators.NoValidator;
import com.beust.jcommander.validators.NoValueValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ParameterDescription {
  private Object m_object;

  private WrappedParameter m_wrappedParameter;
  private Parameter m_parameterAnnotation;
  private DynamicParameter m_dynamicParameterAnnotation;

  /** The field/method */
  private Parameterized m_parameterized;
  /** Keep track of whether a value was added to flag an error */
  private boolean m_assigned = false;
  private ResourceBundle m_bundle;
  private String m_description;
  private JCommander m_jCommander;
  private Object m_default;
  /** Longest of the names(), used to present usage() alphabetically */
  private String m_longestName = "";

  public ParameterDescription(Object object, DynamicParameter annotation,
      Parameterized parameterized,
      ResourceBundle bundle, JCommander jc) {
    if (! Map.class.isAssignableFrom(parameterized.getType())) {
      throw new ParameterException("@DynamicParameter " + parameterized.getName()
          + " should be of type "
          + "Map but is " + parameterized.getType().getName());
    }

    m_dynamicParameterAnnotation = annotation;
    m_wrappedParameter = new WrappedParameter(m_dynamicParameterAnnotation);
    init(object, parameterized, bundle, jc);
  }

  public ParameterDescription(Object object, Parameter annotation, Parameterized parameterized,
      ResourceBundle bundle, JCommander jc) {
    m_parameterAnnotation = annotation;
    m_wrappedParameter = new WrappedParameter(m_parameterAnnotation);
    init(object, parameterized, bundle, jc);
  }

  /**
   * Find the resource bundle in the annotations.
   * @return
   */
  @SuppressWarnings("deprecation")
  private ResourceBundle findResourceBundle(Object o) {
    ResourceBundle result = null;

    Parameters p = o.getClass().getAnnotation(Parameters.class);
    if (p != null && ! isEmpty(p.resourceBundle())) {
      result = ResourceBundle.getBundle(p.resourceBundle(), Locale.getDefault());
    } else {
      com.beust.jcommander.ResourceBundle a = o.getClass().getAnnotation(
          com.beust.jcommander.ResourceBundle.class);
      if (a != null && ! isEmpty(a.value())) {
        result = ResourceBundle.getBundle(a.value(), Locale.getDefault());
      }
    }

    return result;
  }

  private boolean isEmpty(String s) {
    return s == null || "".equals(s);
  }

  private void initDescription(String description, String descriptionKey, String[] names) {
    m_description = description;
    if (! "".equals(descriptionKey)) {
      if (m_bundle != null) {
        m_description = m_bundle.getString(descriptionKey);
      } else {
//        JCommander.getConsole().println("Warning: field " + object.getClass() + "." + field.getName()
//            + " has a descriptionKey but no bundle was defined with @ResourceBundle, using " +
//            "default description:'" + m_description + "'");
      }
    }

    for (String name : names) {
      if (name.length() > m_longestName.length()) m_longestName = name;
    }
  }

  @SuppressWarnings("unchecked")
  private void init(Object object, Parameterized parameterized, ResourceBundle bundle,
      JCommander jCommander) {
    m_object = object;
    m_parameterized = parameterized;
    m_bundle = bundle;
    if (m_bundle == null) {
      m_bundle = findResourceBundle(object);
    }
    m_jCommander = jCommander;

    if (m_parameterAnnotation != null) {
      String description;
      if (Enum.class.isAssignableFrom(parameterized.getType())
          && m_parameterAnnotation.description().isEmpty()) {
        description = "Options: " + EnumSet.allOf((Class<? extends Enum>) parameterized.getType());
      }else {
        description = m_parameterAnnotation.description();
      }
      initDescription(description, m_parameterAnnotation.descriptionKey(),
          m_parameterAnnotation.names());
    } else if (m_dynamicParameterAnnotation != null) {
      initDescription(m_dynamicParameterAnnotation.description(),
          m_dynamicParameterAnnotation.descriptionKey(),
          m_dynamicParameterAnnotation.names());
    } else {
      throw new AssertionError("Shound never happen");
    }

    try {
      m_default = parameterized.get(object);
    } catch (Exception e) {
    }

    //
    // Validate default values, if any and if applicable
    //
    if (m_default != null) {
      if (m_parameterAnnotation != null) {
        validateDefaultValues(m_parameterAnnotation.names());
      }
    }
  }

  private void validateDefaultValues(String[] names) {
    String name = names.length > 0 ? names[0] : "";
    validateValueParameter(name, m_default);
  }

  public String getLongestName() {
    return m_longestName;
  }

  public Object getDefault() {
   return m_default;
  }

  public String getDescription() {
    return m_description;
  }

  public Object getObject() {
    return m_object;
  }

  public String getNames() {
    StringBuilder sb = new StringBuilder();
    String[] names = m_wrappedParameter.names();
    for (int i = 0; i < names.length; i++) {
      if (i > 0) sb.append(", ");
      if (names.length == 1 && names[i].startsWith("--")) sb.append("    ");
      sb.append(names[i]);
    }
    return sb.toString();
  }

  public WrappedParameter getParameter() {
    return m_wrappedParameter;
  }

  public Parameterized getParameterized() {
    return m_parameterized;
  }

  private boolean isMultiOption() {
    Class<?> fieldType = m_parameterized.getType();
    return fieldType.equals(List.class) || fieldType.equals(Set.class)
        || m_parameterized.isDynamicParameter();
  }

  public void addValue(String value) {
    addValue(value, false /* not default */);
  }

  /**
   * @return true if this parameter received a value during the parsing phase.
   */
  public boolean isAssigned() {
    return m_assigned;
  }


  public void setAssigned(boolean b) {
    m_assigned = b;
  }

  /**
   * Add the specified value to the field. First, validate the value if a
   * validator was specified. Then look up any field converter, then any type
   * converter, and if we can't find any, throw an exception.
   */
  public void addValue(String value, boolean isDefault) {
    p("Adding " + (isDefault ? "default " : "") + "value:" + value
        + " to parameter:" + m_parameterized.getName());
    String name = m_wrappedParameter.names()[0];
    if (m_assigned && ! isMultiOption()) {
      throw new ParameterException("Can only specify option " + name + " once.");
    }

    validateParameter(name, value);

    Class<?> type = m_parameterized.getType();

    Object convertedValue = m_jCommander.convertValue(this, value);
    validateValueParameter(name, convertedValue);
    boolean isCollection = Collection.class.isAssignableFrom(type);

    if (isCollection) {
      @SuppressWarnings("unchecked")
      Collection<Object> l = (Collection<Object>) m_parameterized.get(m_object);
      if (l == null || fieldIsSetForTheFirstTime(isDefault)) {
        l = newCollection(type);
        m_parameterized.set(m_object, l);
      }
      if (convertedValue instanceof Collection) {
        l.addAll((Collection) convertedValue);
      } else { // if (isMainParameter || m_parameterAnnotation.arity() > 1) {
        l.add(convertedValue);
//        } else {
//          l.
      }
    } else {
      m_wrappedParameter.addValue(m_parameterized, m_object, convertedValue);
    }
    if (! isDefault) m_assigned = true;
  }

  private void validateParameter(String name, String value) {
    Class<? extends IParameterValidator> validator = m_wrappedParameter.validateWith();
    if (validator != null) {
      validateParameter(this, validator, name, value);
    }
  }

  private void validateValueParameter(String name, Object value) {
    Class<? extends IValueValidator> validator = m_wrappedParameter.validateValueWith();
    if (validator != null) {
      validateValueParameter(validator, name, value);
    }
  }

  public static void validateValueParameter(Class<? extends IValueValidator> validator,
      String name, Object value) {
    try {
      if (validator != NoValueValidator.class) {
        p("Validating value parameter:" + name + " value:" + value + " validator:" + validator);
      }
      validator.newInstance().validate(name, value);
    } catch (InstantiationException e) {
      throw new ParameterException("Can't instantiate validator:" + e);
    } catch (IllegalAccessException e) {
      throw new ParameterException("Can't instantiate validator:" + e);
    }
  }

  public static void validateParameter(ParameterDescription pd,
      Class<? extends IParameterValidator> validator,
      String name, String value) {
    try {
      if (validator != NoValidator.class) {
        p("Validating parameter:" + name + " value:" + value + " validator:" + validator);
      }
      validator.newInstance().validate(name, value);
      if (IParameterValidator2.class.isAssignableFrom(validator)) {
        IParameterValidator2 instance = (IParameterValidator2) validator.newInstance();
        instance.validate(name, value, pd);
      }
    } catch (InstantiationException e) {
      throw new ParameterException("Can't instantiate validator:" + e);
    } catch (IllegalAccessException e) {
      throw new ParameterException("Can't instantiate validator:" + e);
    } catch(ParameterException ex) {
      throw ex;
    } catch(Exception ex) {
      throw new ParameterException(ex);
    }
  }

  /*
   * Creates a new collection for the field's type.
   *
   * Currently only List and Set are supported. Support for
   * Queues and Stacks could be useful.
   */
  @SuppressWarnings("unchecked")
  private Collection<Object> newCollection(Class<?> type) {
    if (SortedSet.class.isAssignableFrom(type)) return new TreeSet();
    else if (LinkedHashSet.class.isAssignableFrom(type)) return new LinkedHashSet();
    else if (Set.class.isAssignableFrom(type)) return new HashSet();
    else if (List.class.isAssignableFrom(type)) return new ArrayList();
    else {
      throw new ParameterException("Parameters of Collection type '" + type.getSimpleName()
                                  + "' are not supported. Please use List or Set instead.");
    }
  }

  /*
   * Tests if its the first time a non-default value is
   * being added to the field.
   */
  private boolean fieldIsSetForTheFirstTime(boolean isDefault) {
    return (!isDefault && !m_assigned);
  }

  private static void p(String string) {
    if (System.getProperty(JCommander.DEBUG_PROPERTY) != null) {
      JCommander.getConsole().println("[ParameterDescription] " + string);
    }
  }

  @Override
  public String toString() {
    return "[ParameterDescription " + m_parameterized.getName() + "]";
  }

  public boolean isDynamicParameter() {
    return m_dynamicParameterAnnotation != null;
  }

  public boolean isHelp() {
    return m_wrappedParameter.isHelp();
  }
}
