package com.beust.jcommander.internal;

public interface Console {

  void print(CharSequence msg);

  void println(CharSequence msg);

  char[] readPassword(boolean echoInput);
}
