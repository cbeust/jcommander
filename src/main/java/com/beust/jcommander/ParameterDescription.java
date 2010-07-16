package com.beust.jcommander;


import com.beust.jcommander.converters.BooleanConverter;
import com.beust.jcommander.converters.IntegerConverter;
import com.beust.jcommander.converters.LongConverter;
import com.beust.jcommander.converters.NoConverter;
import com.beust.jcommander.converters.StringConverter;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class ParameterDescription {
  /**
   * A map of converters per class.
   */
  private static Map<Class<?>, Class<? extends IStringConverter>> m_classConverters
      = new HashMap() {{
    put(String.class, StringConverter.class);
    put(Integer.class, IntegerConverter.class);
    put(int.class, IntegerConverter.class);
    put(Long.class, LongConverter.class);
    put(long.class, LongConverter.class);
    put(Boolean.class, BooleanConverter.class);
    put(boolean.class, BooleanConverter.class);
  }};

  /**
   * A map of converters per field. Will take precedence over the class converter map.
   */
  private Map<Field, IStringConverter> m_fieldConverters = Maps.newHashMap();

  private Object m_object;
  private Parameter m_parameterAnnotation;
  private Field m_field;
  /** Keep track of whether a value was added to flag an error */
  private boolean m_added = false;
  private ResourceBundle m_bundle;
  private String m_description;

  public ParameterDescription(Object object, Parameter annotation, Field field,
      ResourceBundle bundle) {
    init(object, annotation, field, bundle);
  }

  private void init(Object object, Parameter annotation, Field field, ResourceBundle bundle) {
    m_object = object;
    m_parameterAnnotation = annotation;
    m_field = field;
    m_bundle = bundle;
    if (m_bundle == null) {
      com.beust.jcommander.ResourceBundle a
          = object.getClass().getAnnotation(com.beust.jcommander.ResourceBundle.class);
      if (a != null) {
        m_bundle = ResourceBundle.getBundle(a.value(), Locale.getDefault());
      }
    }

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
  }

  public String[] getNames() {
    return m_parameterAnnotation.names();
  }

  public String getDescription() {
    return m_description;
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

  /**
   * Add the specified value to the field. First look up any field converter, then
   * any type converter, and if we can't find any, throw an exception.
   */
  public void addValue(String value) {
    boolean arity = false;
    if (m_added && ! isMultiOption()) {
      throw new ParameterException("Can only specify option " + getNames()[0] + " once.");
    }
    Class<? extends IStringConverter> converterClass = m_parameterAnnotation.converter();
    if (converterClass == NoConverter.class) {
      converterClass = m_classConverters.get(m_field.getType());
    }
    if (converterClass == null && m_parameterAnnotation.arity() >= 2) {
      converterClass = StringConverter.class;
      arity = true;
    }
    if (converterClass == null) {
      throw new ParameterException("Don't know how to convert " + value
          + " to type " + m_field.getType() + " (field: " + m_field.getName() + ")");
    }

    m_added = true;
    IStringConverter converter;
    try {
      converter = converterClass.newInstance();
      Object convertedValue = converter.convert(value);
      if (arity) {
        List l = (List) m_field.get(m_object);
        if (l == null) {
          l = Lists.newArrayList();
          m_field.set(m_object, l);
        }
        l.add(convertedValue);
      } else {
        m_field.set(m_object, convertedValue);
      }
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }
}
