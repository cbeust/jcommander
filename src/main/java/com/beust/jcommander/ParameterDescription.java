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


import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.beust.jcommander.validators.NoValidator;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public class ParameterDescription {
  private Object m_object;
  private Parameter m_parameterAnnotation;
  private Field m_field;
  /** Keep track of whether a value was added to flag an error */
  private boolean m_assigned = false;
  private ResourceBundle m_bundle;
  private String m_description;
  private JCommander m_jCommander;
  private Object m_default;
  /** Longest of the names(), used to present usage() alphabetically */
  private String m_longestName = "";

  public ParameterDescription(Object object, Parameter annotation, Field field,
      ResourceBundle bundle, JCommander jc) {
    init(object, annotation, field, bundle, jc);
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

  private void init(Object object, Parameter annotation, Field field, ResourceBundle bundle,
      JCommander jCommander) {
    m_object = object;
    m_parameterAnnotation = annotation;
    m_field = field;
    m_bundle = bundle;
    if (m_bundle == null) {
      m_bundle = findResourceBundle(object);
    }
    m_jCommander = jCommander;

    m_description = annotation.description();
    if (! "".equals(annotation.descriptionKey())) {
      if (m_bundle != null) {
        m_description = m_bundle.getString(annotation.descriptionKey());
      } else {
//        System.out.println("Warning: field " + object.getClass() + "." + field.getName()
//            + " has a descriptionKey but no bundle was defined with @ResourceBundle, using " +
//            "default description:'" + m_description + "'");
      }
    }

    for (String name : annotation.names()) {
      if (name.length() > m_longestName.length()) m_longestName = name;
    }

    try {
      m_default = m_field.get(m_object);
    } catch (Exception e) {
    }

    //
    // Validate default values, if any and if applicable
    //
    if (m_default != null) {
      String[] names = m_parameterAnnotation.names();
      String name = names.length > 0 ? names[0] : "";
      validateParameter(name, m_default.toString());
    }
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
    String[] names = m_parameterAnnotation.names();
    for (int i = 0; i < names.length; i++) {
      if (i > 0) sb.append(", ");
      if (names.length == 1 && names[i].startsWith("--")) sb.append("    ");
      sb.append(names[i]);
    }
    return sb.toString();
  }

  public Parameter getParameter() {
    return m_parameterAnnotation;
  }

  public Field getField() {
    return m_field;
  }

  private boolean isMultiOption() {
    Class<?> fieldType = m_field.getType();
    return fieldType.equals(List.class) || fieldType.equals(Set.class);
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
    m_assigned = true;
  }

  /**
   * Add the specified value to the field. First, validate the value if a
   * validator was specified. Then look up any field converter, then any type
   * converter, and if we can't find any, throw an exception.
   */
  public void addValue(String value, boolean isDefault) {
    p("Adding " + (isDefault ? "default " : "") + "value:" + value
        + " to parameter:" + m_field.getName());
    String name = m_parameterAnnotation.names()[0];
    if (m_assigned && ! isMultiOption()) {
      throw new ParameterException("Can only specify option " + name
          + " once.");
    }

    validateParameter(name, value);

    Class<?> type = m_field.getType();

    Object convertedValue = m_jCommander.convertValue(this, value);
    boolean isCollection = Collection.class.isAssignableFrom(type);

    try {
      if (isCollection) {
        @SuppressWarnings("unchecked")
        Collection<Object> l = (Collection<Object>) m_field.get(m_object);
        if (l == null || fieldIsSetForTheFirstTime(isDefault)) {
          l = newCollection(type);
          m_field.set(m_object, l);
        }
        if (convertedValue instanceof Collection) {
          l.addAll((Collection) convertedValue);
        } else { // if (isMainParameter || m_parameterAnnotation.arity() > 1) {
          l.add(convertedValue);
//        } else {
//          l.
        }
      } else {
        m_field.set(m_object, convertedValue);
      }
      if (! isDefault) m_assigned = true;
    }
    catch(IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  private void validateParameter(String name, String value) {
    Class<? extends IParameterValidator> validator = m_parameterAnnotation.validateWith();
    if (validator != NoValidator.class) {
      try {
        p("Validating parameter:" + name + " value:" + value + " validator:" + validator);
        validator.newInstance().validate(name, value);
      } catch (InstantiationException e) {
        throw new ParameterException("Can't instantiate validator:" + e);
      } catch (IllegalAccessException e) {
        throw new ParameterException("Can't instantiate validator:" + e);
      }
    }
  }

  /*
   * Creates a new collection for the field's type.
   *
   * Currently only List and Set are supported. Support for
   * Queues and Stacks could be useful.
   */
  private Collection<Object> newCollection(Class<?> type) {
    if(List.class.isAssignableFrom(type)){
      return Lists.newArrayList();
    } else if(Set.class.isAssignableFrom(type)){
      return Sets.newLinkedHashSet();
    } else {
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

  public boolean isNumber() {
    Class<?> type = m_field.getType();
    return type.equals(Integer.class) || type.equals(int.class)
        || type.equals(Long.class) || type.equals(long.class);
  }

  private void p(String string) {
    if (System.getProperty(JCommander.DEBUG_PROPERTY) != null) {
      System.out.println("[ParameterDescription] " + string);
    }
  }

  @Override
  public String toString() {
    return "[ParameterDescription " + m_field.getName() + "]";
  }
}
