package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.stream.Collectors;

@Test
public class ParametersNotEmptyTest {

    public class Args1 {
        @Parameter(names = "-debug", description = "Debug mode")
        public boolean debug = false;

        @Parameter(names = "-date", description = "An ISO 8601 formatted date.")
        public Date date;
    }

    @Test
    public void testParameters() throws Exception {
        final JCommander jc = new JCommander(new Args1());
        final String parameterNames = jc.getParameters().stream()
                .map(ParameterDescription::getNames)
                .sorted()
                .collect(Collectors.joining(", "));
        Assert.assertEquals(parameterNames,"-date, -debug",
                "getParameters returns the @Parameters");
    }
}
