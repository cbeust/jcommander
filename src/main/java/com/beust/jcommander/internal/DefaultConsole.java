package com.beust.jcommander.internal;

import com.beust.jcommander.ParameterException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DefaultConsole implements Console {

  public void print(String msg) {
    System.out.print(msg);
  }

  public void println(String msg) {
    System.out.println(msg);
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
