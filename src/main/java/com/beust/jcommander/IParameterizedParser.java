package com.beust.jcommander;

import java.util.List;

/**
 *
 * @author Tim Gallagher
 */
public interface IParameterizedParser {
  
  /**
   * Parses the given object for any command line related annotations and returns the list of 
   * JCommander Parameterized definitions.
   * 
   * @param arg the object that contains the annotations.
   * @return non-null List but may be empty
   */
  List<Parameterized> parseArg(Object arg);
  
}
