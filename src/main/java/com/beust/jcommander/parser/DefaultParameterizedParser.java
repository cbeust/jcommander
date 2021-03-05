package com.beust.jcommander.parser;

import com.beust.jcommander.IParameterizedParser;
import com.beust.jcommander.Parameterized;
import java.util.List;

/**
 * Pulled from the JCommander where is reflects the object to determine the Parameter annotations.
 *
 * @author Tim Gallagher
 */
public class DefaultParameterizedParser implements IParameterizedParser {

  /**
  * Wraps the default parser. 
  *
  * @param annotatedObj an instance of the object with Parameter related annotations.
  *
  * @author Tim Gallagher
  */
  @Override
  public List<Parameterized> parseArg(Object annotatedObj) {
    return Parameterized.parseArg(annotatedObj);
  }
  
}
