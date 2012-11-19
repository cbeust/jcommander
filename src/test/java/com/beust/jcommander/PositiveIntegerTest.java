package com.beust.jcommander;

import com.beust.jcommander.validators.PositiveInteger;

import org.testng.annotations.Test;

public class PositiveIntegerTest {

  @Test
  public void validateTest() {
    class Arg {
      @Parameter(names = { "-p", "--port" }, description = "Shows help", validateWith = PositiveInteger.class)
      private int port = 0;
    }
    Arg arg = new Arg();
    JCommander jc = new JCommander(arg);
    jc.parse(new String[] { "-p", "8080" });

  }

  @Test(expectedExceptions = ParameterException.class)
  public void validateTest2() {
    class Arg {
      @Parameter(names = { "-p", "--port" }, description = "Shows help", validateWith = PositiveInteger.class)
      private int port = 0;
    }
    Arg arg = new Arg();
    JCommander jc = new JCommander(arg);
    jc.parse(new String[] { "-p", "" });
  }

  @Test(expectedExceptions = ParameterException.class)
  public void validateTest3() {
    class Arg {
      @Parameter(names = { "-p", "--port" }, description = "Shows help", validateWith = PositiveInteger.class)
      private int port = 0;
    }
    Arg arg = new Arg();
    JCommander jc = new JCommander(arg);
    jc.parse(new String[] { "-p", "-1" });
  }

  @Test(expectedExceptions = ParameterException.class)
  public void validateTest4() {
    class Arg {
      @Parameter(names = { "-p", "--port" }, description = "Port Number", validateWith = PositiveInteger.class)
      private int port = 0;
    }
    Arg arg = new Arg();
    JCommander jc = new JCommander(arg);
    jc.parse(new String[] { "-p", "abc" });
  }

  @Test(expectedExceptions = ParameterException.class)
  public void validateTest5() {
    class Arg {
      @Parameter(names = { "-p", "--port" }, description = "Port Number", validateWith = PositiveInteger.class)
      private int port = 0;
    }

    Arg arg = new Arg();
    JCommander jc = new JCommander(arg);
    jc.parse(new String[] { "--port", " " });
  }
}