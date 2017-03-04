package com.beust.jcommander;

/**
 * Created by jeremysolarz on 12/15/16.
 */
public class ArgMultiNameValidator {

    public static class MultiNameValidator implements IValueValidator<String> {

        public static String parsedName;

        public void validate(String name, String value) throws ParameterException {
            parsedName = name;
        }
    }

    @Parameter(names = { "-name1", "-name2" }, description = "Names of parameter", validateValueWith = MultiNameValidator.class, required = true)
    private String parameter;
}
