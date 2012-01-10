package com.beust.jcommander;

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
    return m_parameter != null ? m_parameter.arity() : m_dynamicParameter.arity();
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
}
