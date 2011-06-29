package com.beust.jcommander;

import com.beust.jcommander.converters.FileConverter;

import java.io.File;

public class ArgsValidate2 {
  public static class FailingValidator implements IParameterValidator {

    public void validate(String name, String value) throws ParameterException {
      throw new ParameterException("Validation will always fail:" + name + " " + value);
    }
    
  }

  public static final String POSSIBLE_TEMPLATE_FILE = "mayOrMayNotExist.tempalate";

  @Parameter(names = { "-template"},
      description = "The default file may or may not exist",
      converter = FileConverter.class, 
      validateWith = FailingValidator.class
      )
  public File template = new File(POSSIBLE_TEMPLATE_FILE);
}
