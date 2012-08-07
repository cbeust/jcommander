package com.beust.jcommander;

import org.testng.Assert;


@Parameters(separators = "=")
public class MyClass {

  @Parameter(names = { "-p", "--param" }, validateWith = MyValidator.class)
  private String param;

  public static void main(String[] args) {
    JCommander jCommander = new JCommander(new MyClass());
    jCommander.parse("--param=value");
  }

  public static class MyValidator implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {
      Assert.assertEquals(value, "\"");
    }
  }

}