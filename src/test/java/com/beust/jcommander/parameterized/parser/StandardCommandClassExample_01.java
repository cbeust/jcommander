package com.beust.jcommander.parameterized.parser;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

/**
 * This is an arbitrary class to test Parameter values using standard JCommander annotations.
 *
 * @author Tim Gallagher
 */
public class StandardCommandClassExample_01 {

  public static final String PARAM_VERSION = "version";
  
  @ParametersDelegate
  public final StandardCommandClassExample_02 subCommands = new StandardCommandClassExample_02();

  @Parameter(
    names = { PARAM_VERSION },
    description = "Version of the software to run. eg. \"v38.1.0\"",
    required = true
  )
  public String version;

}
