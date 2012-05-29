package com.beust.jcommander.internal;

public interface Console {

  void print(String msg);

  void println(String msg);

  char[] readPassword(boolean echoInput);
}
