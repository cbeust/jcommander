package com.beust.jcommander.parameterized.parser;

import com.beust.jcommander.Parameter;

/**
 * This is an arbitrary class to test Parameter values using standard JCommander annotations.
 *
 * @author Tim Gallagher
 */
public class StandardCommandClassExample_02 {

  public static final String PARAM_STACK_LEVEL = "stackLevel";
  public static final String PARAM_LOG_LEVEL = "logLevel";
  
  @Parameter(
    names = {PARAM_STACK_LEVEL},
    description = "When reporting an error, how many lines of stack trace."
  )
  public int stackLevel;
  
  @Parameter(
    names = {PARAM_LOG_LEVEL},
    description = "Determines which logging messages are printed out."
  )
  public String loggingLevel;

}
