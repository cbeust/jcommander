package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

/**
 * Test multi-object parsing, along with ArgsSlave.
 * 
 * @author cbeust
 */
public class ArgsMaster {
  @Parameter(names = "-master")
  public String master;
}
