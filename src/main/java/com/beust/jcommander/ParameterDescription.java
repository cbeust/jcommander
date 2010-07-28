package com.beust.jcommander;


import com.beust.jcommander.converters.NoConverter;
import com.beust.jcommander.converters.StringConverter;
import com.beust.jcommander.internal.Lists;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

  public ParameterDescription(Object object, Parameter annotation, Field field,
      ResourceBundle bundle, JCommander jc) {
    init(object, annotation, field, bundle, jc);
  }

  /**
   * Find the resource bundle in the annotations.
   * @return
   */
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
  }

  public String getDescription() {
    return m_description;
  }

  public Object getObject() {
    return m_object;
  }

  public String getNames() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < m_parameterAnnotation.names().length; i++) {
      if (i > 0) sb.append(", ");
      sb.append(m_parameterAnnotation.names()[i]);
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
   * Add the specified value to the field. First look up any field converter, then
   * any type converter, and if we can't find any, throw an exception.
   * 
   * @param markAdded if true, mark this parameter as assigned
   */
  public void addValue(String value, boolean isDefault) {
    log("Adding " + (isDefault ? "default " : "") + "value:" + value
        + " to parameter:" + m_field.getName());
    boolean isCollection = false;
    if (m_assigned && ! isMultiOption()) {
      throw new ParameterException("Can only specify option " + m_parameterAnnotation.names()[0]
          + " once.");
    }

    Class<?> type = m_field.getType();
    Class<? extends IStringConverter<?>> converterClass = null;

    //
    // Try to find a converter on the annotation
    //
    converterClass = m_parameterAnnotation.converter();
    if (converterClass == NoConverter.class) {
      converterClass = m_jCommander.findConverter(type);
    }
    if (converterClass == null && m_parameterAnnotation.arity() >= 2) {
      converterClass = StringConverter.class;
      isCollection = true;
    }
    if (converterClass == null && Collection.class.isAssignableFrom(type)) {
      converterClass = StringConverter.class;
      isCollection = true;
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

    if (converterClass == null) {
      throw new ParameterException("Don't know how to convert " + value
          + " to type " + type + " (field: " + m_field.getName() + ")");
    }

    if (! isDefault) m_assigned = true;

    try {
      IStringConverter<?> converter = instantiateConverter(converterClass);
      Object convertedValue = converter.convert(value);
      if (isCollection) {
        @SuppressWarnings("unchecked")
        List<Object> l = (List<Object>) m_field.get(m_object);
        if (l == null) {
          l = Lists.newArrayList();
          m_field.set(m_object, l);
        }
        l.add(convertedValue);
      } else {
        m_field.set(m_object, convertedValue);
      }
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  public boolean isNumber() {
    Class<?> type = m_field.getType();
    return type.equals(Integer.class) || type.equals(int.class)
        || type.equals(Long.class) || type.equals(long.class);
  }

  private IStringConverter<?> instantiateConverter(
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
        ? stringCtor.newInstance(m_parameterAnnotation.names()[0])
        : ctor.newInstance();

        return result;
  }

  private void log(String string) {
    if (System.getProperty(JCommander.DEBUG_PROPERTY) != null) {
      System.out.println("[ParameterDescription] " + string);
    }
  }
}
