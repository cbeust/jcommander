package com.beust.jcommander;

/**
 * Test multi-object parsing, along with ArgsSlave.
 * 
 * @author cbeust
 */
public class ArgsSlave {
  @Parameter(names = "-slave")
  public String slave;
}
