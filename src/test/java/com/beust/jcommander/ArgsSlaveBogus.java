package com.beust.jcommander;

/**
 * Same as ArgsMaster class, should cause an error.
 * 
 * @author cbeust
 */
public class ArgsSlaveBogus extends ArgsSlave {
  @Parameter(names = "-master")
  public String master;
}
