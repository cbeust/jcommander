package com.beust.jcommander;

import com.beust.jcommander.converters.FileConverter;

import java.io.File;

public class ArgsValidate2 {
  public static class FailingValidator implements IValueValidator<File> {

    public void validate(String name, File value) throws ParameterException {
      throw new ParameterException("Validation will always fail:" + name + " " + value);
    }
    
  }

  public static final String POSSIBLE_TEMPLATE_FILE = "mayOrMayNotExist.template";

  @Parameter(names = { "-template"},
      description = "The default file may or may not exist",
      converter = FileConverter.class, 
      validateValueWith = FailingValidator.class
      )
  public File template = new File(POSSIBLE_TEMPLATE_FILE);
}
