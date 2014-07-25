package com.beust.jcommander;

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

  public Parameter getParameter() {
    return m_parameter;
  }

  public DynamicParameter getDynamicParameter() {
    return m_dynamicParameter;
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

  public Class<? extends IValueValidator> validateValueWith() {
    return m_parameter != null
        ? m_parameter.validateValueWith()
        : m_dynamicParameter.validateValueWith();
  }

  public boolean echoInput() {
	  return m_parameter != null ? m_parameter.echoInput() : false;
  }

  public void addValue(Parameterized parameterized, Object object, Object value) {
    if (m_parameter != null) {
      parameterized.set(object, value);
    } else {
      String a = m_dynamicParameter.assignment();
      String sv = value.toString();

      int aInd = sv.indexOf(a);
      if (aInd == -1) {
        throw new ParameterException(
            "Dynamic parameter expected a value of the form a" + a + "b"
                + " but got:" + sv);
      }
      callPut(object, parameterized, sv.substring(0, aInd), sv.substring(aInd + 1));
    }
  }

  private void callPut(Object object, Parameterized parameterized, String key, String value) {
    try {
      Method m;
      m = findPut(parameterized.getType());
      m.invoke(parameterized.get(object), key, value);
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

  public boolean isHelp() {
    return m_parameter != null && m_parameter.help();
  }

  public boolean isNonOverwritableForced() {
      return m_parameter != null && m_parameter.forceNonOverwritable();
  }
}
