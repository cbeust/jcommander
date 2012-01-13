package com.beust.jcommander;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Encapsulates the operations common to @Parameter and @DynamicParameter
 */
public class WrappedParameter {
  private Parameter m_parameter;
  private DynamicParameter m_dynamicParameter;

  public WrappedParameter(Parameter p) {
    m_parameter = p;
  }

  public WrappedParameter(DynamicParameter p) {
    m_dynamicParameter = p;
  }

  public int arity() {
    return m_parameter != null ? m_parameter.arity() : 1;
  }

  public boolean hidden() {
    return m_parameter != null ? m_parameter.hidden() : m_dynamicParameter.hidden();
  }

  public boolean required() {
    return m_parameter != null ? m_parameter.required() : m_dynamicParameter.required();
  }

  public boolean password() {
    return m_parameter != null ? m_parameter.password() : false;
  }

  public String[] names() {
    return m_parameter != null ? m_parameter.names() : m_dynamicParameter.names();
  }

  public boolean variableArity() {
    return m_parameter != null ? m_parameter.variableArity() : false;
  }

  public Class<? extends IParameterValidator> validateWith() {
    return m_parameter != null ? m_parameter.validateWith() : m_dynamicParameter.validateWith();
  }

  public void addValue(Field field, Object object, Object value)
      throws IllegalArgumentException, IllegalAccessException {
    if (m_parameter != null) {
      field.set(object, value);
    } else {
      String a = m_dynamicParameter.assignment();
      String sv = value.toString();
      String[] kv = sv.split(a);
      if (kv.length != 2) {
        throw new ParameterException("Dynamic parameter expected a value of the form a" + a + "b"
            + " but got:" + sv);
      }
      callPut(object, field, kv[0], kv[1]);
    }
  }

  private void callPut(Object object, Field field, String key, String value) {
    try {
      Method m;
      m = findPut(field.getType());
      m.invoke(field.get(object), key, value);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch(IllegalAccessException e) {
      e.printStackTrace();
    } catch(InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  private Method findPut(Class<?> cls) throws SecurityException, NoSuchMethodException {
    return cls.getMethod("put", Object.class, Object.class);
  }

  public String getAssignment() {
    return m_dynamicParameter != null ? m_dynamicParameter.assignment() : "";
  }
}
