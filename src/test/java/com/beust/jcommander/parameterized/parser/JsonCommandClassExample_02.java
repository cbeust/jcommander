package com.beust.jcommander.parameterized.parser;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is an arbitrary class to test Parameter values using JSON annotations instead with 
 * JCommander annotations.
 *
 * @author Tim Gallagher
 */
public class JsonCommandClassExample_02 {

  public static final String PARAM_STACK_LEVEL = "stackLevel";
  public static final String PARAM_LOG_LEVEL = "logLevel";
  
  @JsonProperty(
    value = PARAM_STACK_LEVEL
  )  
  public int stackLevel;
  
  @JsonProperty(
    value = PARAM_LOG_LEVEL
  )  
  public String loggingLevel;

}
