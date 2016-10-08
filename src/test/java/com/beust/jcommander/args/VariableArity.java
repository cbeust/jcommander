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
  public List<String> main = new ArrayList<>();

  @Parameter(names = "-variable", variableArity = true)
  public List<String> var = new ArrayList<>();

  public int processVariableArity(String optionName, String[] options) {
    return m_count;
  }
}
