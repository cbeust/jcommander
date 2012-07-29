package com.beust.jcommander;

@Parameters(separators = "=")
public class MyClass {

  @Parameter(names = { "-p", "--param" })
  private String param;

  public static void main(String[] args) {
    JCommander jCommander = new JCommander(new MyClass());
    jCommander.parse("-p=\"");
  }

}