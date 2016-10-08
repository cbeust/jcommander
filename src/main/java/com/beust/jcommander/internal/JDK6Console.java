package com.beust.jcommander.internal;

import com.beust.jcommander.ParameterException;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class JDK6Console implements Console {

  private Object console;

  private PrintWriter writer;

  public JDK6Console(Object console) throws Exception {
    this.console = console;
    Method writerMethod = console.getClass().getDeclaredMethod("writer");
    writer = (PrintWriter) writerMethod.invoke(console);
  }

  public void print(String msg) {
    writer.print(msg);
  }

  public void println(String msg) {
    writer.println(msg);
  }

  public char[] readPassword(boolean echoInput) {
    try {
      writer.flush();
      Method method;
      if (echoInput) {
          method = console.getClass().getDeclaredMethod("readLine");
          return ((String) method.invoke(console)).toCharArray();
      } else {
          method = console.getClass().getDeclaredMethod("readPassword");
          return (char[]) method.invoke(console);
      }
    }
    catch (Exception e) {
      throw new ParameterException(e);
    }
  }

}