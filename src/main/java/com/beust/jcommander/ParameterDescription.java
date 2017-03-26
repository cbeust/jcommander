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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.ResourceBundle;

public class ParameterDescription {
  private Object object;

  private WrappedParameter wrappedParameter;
  private Parameter parameterAnnotation;
  private DynamicParameter dynamicParameterAnnotation;

  /** The field/method */
  private Parameterized parameterized;
  /** Keep track of whether a value was added to flag an error */
  private boolean assigned = false;
  private ResourceBundle bundle;
  private String description;
  private JCommander jCommander;
  private Object defaultObject;
  /** Longest of the names(), used to present usage() alphabetically */
  private String longestName = "";

  public ParameterDescription(Object object, DynamicParameter annotation,
      Parameterized parameterized,
      ResourceBundle bundle, JCommander jc) {
    if (! Map.class.isAssignableFrom(parameterized.getType())) {
      throw new ParameterException("@DynamicParameter " + parameterized.getName()
          + " should be of type "
          + "Map but is " + parameterized.getType().getName());
    }

    dynamicParameterAnnotation = annotation;
    wrappedParameter = new WrappedParameter(dynamicParameterAnnotation);
    init(object, parameterized, bundle, jc);
  }

  public ParameterDescription(Object object, Parameter annotation, Parameterized parameterized,
      ResourceBundle bundle, JCommander jc) {
    parameterAnnotation = annotation;
    wrappedParameter = new WrappedParameter(parameterAnnotation);
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
    this.description = description;
    if (! "".equals(descriptionKey)) {
      if (bundle != null) {
        this.description = bundle.getString(descriptionKey);
      }
    }

    for (String name : names) {
      if (name.length() > longestName.length()) longestName = name;
    }
  }

  @SuppressWarnings("unchecked")
  private void init(Object object, Parameterized parameterized, ResourceBundle bundle,
      JCommander jCommander) {
    this.object = object;
    this.parameterized = parameterized;
    this.bundle = bundle;
    if (this.bundle == null) {
      this.bundle = findResourceBundle(object);
    }
    this.jCommander = jCommander;

    if (parameterAnnotation != null) {
      String description;
      if (Enum.class.isAssignableFrom(parameterized.getType())
          && parameterAnnotation.description().isEmpty()) {
        description = "Options: " + EnumSet.allOf((Class<? extends Enum>) parameterized.getType());
      }else {
        description = parameterAnnotation.description();
      }
      initDescription(description, parameterAnnotation.descriptionKey(),
          parameterAnnotation.names());
    } else if (dynamicParameterAnnotation != null) {
      initDescription(dynamicParameterAnnotation.description(),
          dynamicParameterAnnotation.descriptionKey(),
          dynamicParameterAnnotation.names());
    } else {
      throw new AssertionError("Shound never happen");
    }

    try {
      defaultObject = parameterized.get(object);
    } catch (Exception e) {
    }

    //
    // Validate default values, if any and if applicable
    //
    if (defaultObject != null) {
      if (parameterAnnotation != null) {
        validateDefaultValues(parameterAnnotation.names());
      }
    }
  }

  private void validateDefaultValues(String[] names) {
    String name = names.length > 0 ? names[0] : "";
    validateValueParameter(name, defaultObject);
  }

  public String getLongestName() {
    return longestName;
  }

  public Object getDefault() {
   return defaultObject;
  }

  public String getDescription() {
    return description;
  }

  public Object getObject() {
    return object;
  }

  public String getNames() {
    StringBuilder sb = new StringBuilder();
    String[] names = wrappedParameter.names();
    for (int i = 0; i < names.length; i++) {
      if (i > 0) sb.append(", ");
      sb.append(names[i]);
    }
    return sb.toString();
  }

  public WrappedParameter getParameter() {
    return wrappedParameter;
  }

  public Parameterized getParameterized() {
    return parameterized;
  }

  private boolean isMultiOption() {
    Class<?> fieldType = parameterized.getType();
    return fieldType.equals(List.class) || fieldType.equals(Set.class)
        || parameterized.isDynamicParameter();
  }

  public void addValue(String value) {
    addValue(value, false /* not default */);
  }

  /**
   * @return true if this parameter received a value during the parsing phase.
   */
  public boolean isAssigned() {
    return assigned;
  }


  public void setAssigned(boolean b) {
    assigned = b;
  }

  /**
   * Add the specified value to the field. First, validate the value if a
   * validator was specified. Then look up any field converter, then any type
   * converter, and if we can't find any, throw an exception.
   */
  public void addValue(String value, boolean isDefault) {
    addValue(null, value, isDefault, true, -1);
  }

  Object addValue(String name, String value, boolean isDefault, boolean validate, int currentIndex) {
    p("Adding " + (isDefault ? "default " : "") + "value:" + value
        + " to parameter:" + parameterized.getName());
    if(name == null) {
      name = wrappedParameter.names()[0];
    }
    if (currentIndex == 00 && assigned && ! isMultiOption() && !jCommander.isParameterOverwritingAllowed()
            || isNonOverwritableForced()) {
      throw new ParameterException("Can only specify option " + name + " once.");
    }

    if (validate) {
      validateParameter(name, value);
    }

    Class<?> type = parameterized.getType();

    Object convertedValue = jCommander.convertValue(getParameterized(), getParameterized().getType(), name, value);
    if (validate) {
      validateValueParameter(name, convertedValue);
    }
    boolean isCollection = Collection.class.isAssignableFrom(type);

    Object finalValue;
    if (isCollection) {
      @SuppressWarnings("unchecked")
      Collection<Object> l = (Collection<Object>) parameterized.get(object);
      if (l == null || fieldIsSetForTheFirstTime(isDefault)) {
          l = newCollection(type);
          parameterized.set(object, l);
      }
      if (convertedValue instanceof Collection) {
          l.addAll((Collection) convertedValue);
      } else {
          l.add(convertedValue);
      }
      finalValue = l;
    } else {
      // If the field type is not a collection, see if it's a type that contains @SubParameters annotations
      List<SubParameterIndex> subParameters = findSubParameters(type);
      if (! subParameters.isEmpty()) {
        // @SubParameters found
        finalValue = handleSubParameters(value, currentIndex, type, subParameters);
      } else {
        // No, regular parameter
        wrappedParameter.addValue(parameterized, object, convertedValue);
        finalValue = convertedValue;
      }
    }
    if (! isDefault) assigned = true;

    return finalValue;
  }

  private Object handleSubParameters(String value, int currentIndex, Class<?> type,
      List<SubParameterIndex> subParameters) {
    Object finalValue;// Yes, assign each following argument to the corresponding field of that object
    SubParameterIndex sai = null;
    for (SubParameterIndex si: subParameters) {
      if (si.order == currentIndex) {
        sai = si;
        break;
      }
    }
    if (sai != null) {
      Object objectValue = parameterized.get(object);
      try {
        if (objectValue == null) {
          objectValue = type.newInstance();
          parameterized.set(object, objectValue);
        }
        wrappedParameter.addValue(parameterized, objectValue, value, sai.field);
        finalValue = objectValue;
      } catch (InstantiationException | IllegalAccessException e) {
        throw new ParameterException("Couldn't instantiate " + type, e);
      }
    } else {
      throw new ParameterException("Couldn't find where to assign parameter " + value + " in " + type);
    }
    return finalValue;
  }

  public Parameter getParameterAnnotation() {
    return parameterAnnotation;
  }

  class SubParameterIndex {
    int order = -1;
    Field field;

    public SubParameterIndex(int order, Field field) {
      this.order = order;
      this.field = field;
    }
  }

  private List<SubParameterIndex> findSubParameters(Class<?> type) {
    List<SubParameterIndex> result = new ArrayList<>();
    for (Field field: type.getDeclaredFields()) {
      Annotation subParameter = field.getAnnotation(SubParameter.class);
      if (subParameter != null) {
        SubParameter sa = (SubParameter) subParameter;
        result.add(new SubParameterIndex(sa.order(), field));
      }
    }
    return result;
  }

  private void validateParameter(String name, String value) {
    final Class<? extends IParameterValidator> validators[] = wrappedParameter.validateWith();
    if (validators != null && validators.length > 0) {
        for(final Class<? extends IParameterValidator> validator: validators) {
          validateParameter(this, validator, name, value);
        }
    }
  }

  void validateValueParameter(String name, Object value) {
    final Class<? extends IValueValidator> validators[] = wrappedParameter.validateValueWith();
    if (validators != null && validators.length > 0) {
      for(final Class<? extends IValueValidator> validator: validators) {
        validateValueParameter(validator, name, value);
      }
    }
  }

  public static void validateValueParameter(Class<? extends IValueValidator> validator,
      String name, Object value) {
    try {
      if (validator != NoValueValidator.class) {
        p("Validating value parameter:" + name + " value:" + value + " validator:" + validator);
      }
      validator.newInstance().validate(name, value);
    } catch (InstantiationException | IllegalAccessException e) {
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
    } catch (InstantiationException | IllegalAccessException e) {
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
    return (!isDefault && !assigned);
  }

  private static void p(String string) {
    if (System.getProperty(JCommander.DEBUG_PROPERTY) != null) {
      JCommander.getConsole().println("[ParameterDescription] " + string);
    }
  }

  @Override
  public String toString() {
    return "[ParameterDescription " + parameterized.getName() + "]";
  }

  public boolean isDynamicParameter() {
    return dynamicParameterAnnotation != null;
  }

  public boolean isHelp() {
    return wrappedParameter.isHelp();
  }
  
  public boolean isNonOverwritableForced() {
    return wrappedParameter.isNonOverwritableForced();
  }
}
