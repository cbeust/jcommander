package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

/**
 * Same as ArgsMaster class, should cause an error.
 * 
 * @author cbeust
 */
public class ArgsSlaveBogus extends ArgsSlave {
  @Parameter(names = "-master")
  public String master;
}
