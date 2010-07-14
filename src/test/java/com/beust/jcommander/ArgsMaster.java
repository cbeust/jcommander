package com.beust.jcommander;

/**
 * Test multi-object parsing, along with ArgsSlave.
 * 
 * @author cbeust
 */
public class ArgsMaster {
  @Parameter(names = "-master")
  public String master;
}
