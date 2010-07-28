package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

/**
 * Error case if multiple unparsed (without a names attribute) arguments are defined.
 * 
 * @author cbeust
 */
public class ArgsMultipleUnparsed {

  @Parameter(description = "Bogus1")
  public String unparsed1;

  @Parameter(description = "Bogus2")
  public String unparsed2;
}
