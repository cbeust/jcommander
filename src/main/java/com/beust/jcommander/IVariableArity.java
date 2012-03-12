package com.beust.jcommander;

/**
 * Must be implemented by argument classes that contain at least one
 * \@Parameter with "variableArity = true".
 */
public interface IVariableArity {

  /**
   * @param optionName the name of the option to process.
   * @param options the entire list of options.
   *
   * @return how many options were processed.
   */
  int processVariableArity(String optionName, String[] options);
}
