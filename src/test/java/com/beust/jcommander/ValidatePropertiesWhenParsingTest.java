package com.beust.jcommander;

import org.testng.annotations.Test;

public class ValidatePropertiesWhenParsingTest {
  @Test
  public void f()
      throws Exception {

    JCommander cmd = new JCommander();

    cmd.addCommand("a", new A());

    cmd.parse("a", "-path", "myPathToHappiness");
  }

  public static class MyPathValidator implements IParameterValidator {

    public void validate(String name, String value) throws ParameterException {
      throw new RuntimeException("I shouldn't be called for command A!");
    }
  }

  @Parameters
  public static class A {

    @Parameter(names = "-path")
    private String path = "W";
  }

  @Parameters
  public static class B {

    @Parameter(names = "-path", validateWith = MyPathValidator.class)
    private String path = "W";
  }

  public static void main(String[] args) throws Exception {
    new ValidatePropertiesWhenParsingTest().f();
  }
}