package com.beust.jcommander;

import com.beust.jcommander.internal.Lists;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Encapsulate a field or a method annotated with @Parameter or @DynamicParameter
 */
public class Parameterized {

  // Either a method or a field
  private Field m_field;
  private Method m_method;
  private Method m_getter;

  // Either of these two
  private WrappedParameter m_wrappedParameter;
  private ParametersDelegate m_parametersDelegate;

  public Parameterized(WrappedParameter wp, ParametersDelegate pd,
      Field field, Method method) {
    m_wrappedParameter = wp;
    m_method = method;
    m_field = field;
    if (m_field != null) {
      m_field.setAccessible(true);
    }
    m_parametersDelegate = pd;
  }

  public static List<Parameterized> parseArg(Object arg) {
    List<Parameterized> result = Lists.newArrayList();

    Class<? extends Object> cls = arg.getClass();
    while (!Object.class.equals(cls)) {
      for (Field f : cls.getDeclaredFields()) {
        Annotation annotation = f.getAnnotation(Parameter.class);
        Annotation delegateAnnotation = f.getAnnotation(ParametersDelegate.class);
        Annotation dynamicParameter = f.getAnnotation(DynamicParameter.class);
        if (annotation != null) {
          result.add(new Parameterized(new WrappedParameter((Parameter) annotation), null,
              f, null));
        } else if (dynamicParameter != null) {
          result.add(new Parameterized(new WrappedParameter((DynamicParameter) dynamicParameter), null,
              f, null));
        } else if (delegateAnnotation != null) {
          result.add(new Parameterized(null, (ParametersDelegate) delegateAnnotation,
              f, null));
        }
      }
      cls = cls.getSuperclass();
    }

    // Reassigning
    cls = arg.getClass();
    while (!Object.class.equals(cls)) {
      for (Method m : cls.getDeclaredMethods()) {
        Annotation annotation = m.getAnnotation(Parameter.class);
        Annotation delegateAnnotation = m.getAnnotation(ParametersDelegate.class);
        Annotation dynamicParameter = m.getAnnotation(DynamicParameter.class);
        if (annotation != null) {
          result.add(new Parameterized(new WrappedParameter((Parameter) annotation), null,
              null, m));
        } else if (dynamicParameter != null) {
          result.add(new Parameterized(new WrappedParameter((DynamicParameter) annotation), null,
              null, m));
        } else if (delegateAnnotation != null) {
          result.add(new Parameterized(null, (ParametersDelegate) delegateAnnotation,
              null, m));
        }
      }
      cls = cls.getSuperclass();
    }

    return result;
  }

  public WrappedParameter getWrappedParameter() {
    return m_wrappedParameter;
  }

  public Class<?> getType() {
    if (m_method != null) {
      return m_method.getParameterTypes()[0];
    } else {
      return m_field.getType();
    }
  }

  public String getName() {
    if (m_method != null) {
      return m_method.getName();
    } else {
      return m_field.getName();
    }
  }

  public Object get(Object object) {
    try {
      if (m_method != null) {
        if (m_getter == null) {
            m_getter = m_method.getDeclaringClass()
                .getMethod("g" + m_method.getName().substring(1),
                new Class[0]);
        }
        return m_getter.invoke(object);
      } else {
        return m_field.get(object);
      }
    } catch (SecurityException e) {
      throw new ParameterException(e);
    } catch (NoSuchMethodException e) {
      // Try to find a field
      String name = m_method.getName();
      String fieldName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
      Object result = null;
      try {
        Field field = m_method.getDeclaringClass().getDeclaredField(fieldName);
        if (field != null) {
          field.setAccessible(true);
          result = field.get(object);
        }
      } catch(NoSuchFieldException ex) {
        // ignore
      } catch(IllegalAccessException ex) {
        // ignore
      }
      return result;
    } catch (IllegalArgumentException e) {
      throw new ParameterException(e);
    } catch (IllegalAccessException e) {
      throw new ParameterException(e);
    } catch (InvocationTargetException e) {
      throw new ParameterException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((m_field == null) ? 0 : m_field.hashCode());
    result = prime * result + ((m_method == null) ? 0 : m_method.hashCode());
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
    Parameterized other = (Parameterized) obj;
    if (m_field == null) {
      if (other.m_field != null)
        return false;
    } else if (!m_field.equals(other.m_field))
      return false;
    if (m_method == null) {
      if (other.m_method != null)
        return false;
    } else if (!m_method.equals(other.m_method))
      return false;
    return true;
  }

  public boolean isDynamicParameter(Field field) {
    if (m_method != null) {
      return m_method.getAnnotation(DynamicParameter.class) != null;
    } else {
      return m_field.getAnnotation(DynamicParameter.class) != null;
    }
  }

  public void set(Object object, Object value) {
    try {
      if (m_method != null) {
        m_method.invoke(object, value);
      } else {
          m_field.set(object, value);
      }
    } catch (IllegalArgumentException ex) {
      throw new ParameterException(ex);
    } catch (IllegalAccessException ex) {
      throw new ParameterException(ex);
    } catch (InvocationTargetException ex) {
      // If a ParameterException was thrown, don't wrap it into another one
      if (ex.getTargetException() instanceof ParameterException) {
        throw (ParameterException) ex.getTargetException();
      } else {
        throw new ParameterException(ex);
      }
    }
  }

  public ParametersDelegate getDelegateAnnotation() {
    return m_parametersDelegate;
  }

  public Type getGenericType() {
    if (m_method != null) {
      return m_method.getGenericParameterTypes()[0];
    } else {
      return m_field.getGenericType();
    }
  }

  public Parameter getParameter() {
    return m_wrappedParameter.getParameter();
  }

  /**
   * @return the generic type of the collection for this field, or null if not applicable.
   */
  public Type findFieldGenericType() {
    if (m_method != null) {
      return null;
    } else {
      if (m_field.getGenericType() instanceof ParameterizedType) {
        ParameterizedType p = (ParameterizedType) m_field.getGenericType();
        Type cls = p.getActualTypeArguments()[0];
        if (cls instanceof Class) {
          return cls;
        }
      }
    }

    return null;
  }

  public boolean isDynamicParameter() {
    return m_wrappedParameter.getDynamicParameter() != null;
  }

}
