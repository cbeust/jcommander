package com.beust.jcommander;

import static java.lang.Boolean.TRUE;

import java.util.Map;

import org.testng.annotations.Test;

public class ParametersRulesTest {

    @Parameters(parametersValidators = QuietAndVerboseAreMutualExclusive.class)
    class Flags {
        @Parameter(names = "--quiet", description = "Do not output anything")
        boolean quiet;

        @Parameter(names = "--verbose", description = "Output detailed information")
        boolean verbose;
    }

    public static class QuietAndVerboseAreMutualExclusive implements IParametersValidator {
        @Override
        public void validate(Map<String, Object> parameters) throws ParameterException {
            if (parameters.get("--quiet") == TRUE && parameters.get("--verbose") == TRUE)
                throw new ParameterException("--quiet and --verbose are mutually exclusive");
        }
    }

    @Test(expectedExceptions = ParameterException.class,
          expectedExceptionsMessageRegExp = "--quiet and --verbose are mutually exclusive")
    public void testParameters() throws Exception {
        Object o = new Flags();
        JCommander.newBuilder().addObject(o).build().parse("--quiet", "--verbose");
    }

}
