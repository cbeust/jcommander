package com.beust.jcommander;

public class ParameterException extends RuntimeException {

  public ParameterException(Throwable t) {
    super(t);
  }

  public ParameterException(String string) {
    super(string);
  }

}
