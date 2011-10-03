package com.beust.jcommander.args;

import com.beust.jcommander.IVariableArity;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class VariableArity implements IVariableArity {

  private int m_count;

  public VariableArity(int count) {
    m_count = count;
  }

  @Parameter
  public List<String> main = new ArrayList<String>();

  @Parameter(names = "-variable", variableArity = true)
  public List<String> var = new ArrayList<String>();

  public int processVariableArity(String optionName, String[] options) {
    for (int i = 0; i < m_count; i++) {
      var.add(options[i]);
    }
    return m_count;
  }
}
