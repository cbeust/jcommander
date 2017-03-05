package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        JCommander jc = new JCommander(new Args1());
        List<String> parameters = new ArrayList<>();
        for (ParameterDescription pd : jc.getParameters()) {
            parameters.add(pd.getNames());
        }
        Collections.sort(parameters);

        Assert.assertEquals(parameters, new ArrayList<String>() {{
            add("-date");
            add("-debug");
        }}
        );
    }
}
