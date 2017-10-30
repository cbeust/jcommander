package com.beust.jcommander;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Encapsulates the operations common to @Parameter and @DynamicParameter
 */
public class WrappedParameter {
  private Parameter parameter;
  private DynamicParameter dynamicParameter;

  public WrappedParameter(Parameter p) {
    parameter = p;
  }

  public WrappedParameter(DynamicParameter p) {
    dynamicParameter = p;
  }

  public Parameter getParameter() {
    return parameter;
  }

  public DynamicParameter getDynamicParameter() {
    return dynamicParameter;
  }

  public int arity() {
    return parameter != null ? parameter.arity() : 1;
  }

  public boolean hidden() {
    return parameter != null ? parameter.hidden() : dynamicParameter.hidden();
  }

  public boolean required() {
    return parameter != null ? parameter.required() : dynamicParameter.required();
  }

  public boolean password() {
    return parameter != null ? parameter.password() : false;
  }

  public String[] names() {
    return parameter != null ? parameter.names() : dynamicParameter.names();
  }

  public boolean variableArity() {
    return parameter != null ? parameter.variableArity() : false;
  }

  public int order() {
    return parameter != null ? parameter.order() : dynamicParameter.order();
  }

  public Class<? extends IParameterValidator>[] validateWith() {
    return parameter != null ? parameter.validateWith() : dynamicParameter.validateWith();
  }

  public Class<? extends IValueValidator>[] validateValueWith() {
    return parameter != null
        ? parameter.validateValueWith()
        : dynamicParameter.validateValueWith();
  }

  public boolean echoInput() {
    return parameter != null ? parameter.echoInput() : false;
  }

  public void addValue(Parameterized parameterized, Object object, Object value) {
    try {
      addValue(parameterized, object, value, null);
    } catch (IllegalAccessException e) {
      throw new ParameterException("Couldn't set " + object + " to " + value, e);
    }
  }

  public void addValue(Parameterized parameterized, Object object, Object value, Field field)
          throws IllegalAccessException {
    if (parameter != null) {
      if (field != null) {
        field.set(object, value);
      } else {
        parameterized.set(object, value);
      }
    } else {
      String a = dynamicParameter.assignment();
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
    } catch (SecurityException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  private Method findPut(Class<?> cls) throws SecurityException, NoSuchMethodException {
    return cls.getMethod("put", Object.class, Object.class);
  }

  public String getAssignment() {
    return dynamicParameter != null ? dynamicParameter.assignment() : "";
  }

  public boolean isHelp() {
    return parameter != null && parameter.help();
  }

  public boolean isNonOverwritableForced() {
      return parameter != null && parameter.forceNonOverwritable();
  }
}
