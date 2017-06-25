package com.beust.jcommander.internal;

import com.beust.jcommander.ParameterException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class DefaultConsole implements Console {
  private final PrintStream target;

  public DefaultConsole(PrintStream target) {
    this.target = target;
  }

  public DefaultConsole() {
    this.target = System.out;
  }

  public void print(String msg) {
    target.print(msg);
  }

  public void println(String msg) {
    target.println(msg);
  }

  public char[] readPassword(boolean echoInput) {
    try {
      // Do not close the readers since System.in should not be closed
      InputStreamReader isr = new InputStreamReader(System.in);
      BufferedReader in = new BufferedReader(isr);
      String result = in.readLine();
      return result.toCharArray();
    }
    catch (IOException e) {
      throw new ParameterException(e);
    }
  }

}
