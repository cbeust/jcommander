package com.beust.jcommander.internal;

import com.beust.jcommander.ParameterException;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class JDK6Console implements Console {

  private Object console;

  private PrintWriter writer;

  public JDK6Console(Object console) throws Exception {
    this.console = console;
    Method writerMethod = console.getClass().getDeclaredMethod("writer", new Class<?>[0]);
    writer = (PrintWriter) writerMethod.invoke(console, new Object[0]);
  }

  public void print(String msg) {
    writer.print(msg);
  }

  public void println(String msg) {
    writer.println(msg);
  }

  public char[] readPassword() {
    try {
      writer.flush();
      Method readPasswordMethod = console.getClass().getDeclaredMethod("readPassword", new Class<?>[0]);
      return (char[]) readPasswordMethod.invoke(console, new Object[0]);
    }
    catch (Exception e) {
      throw new ParameterException(e);
    }
  }

}
