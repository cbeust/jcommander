package com.beust.jcommander;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

@Parameters(separators = "=", commandDescription = "Just for testing quote handling.")
public class TestParameterQuoteHandling {

    @Parameter(names = { "--aParameter" }, description = "A String.")
    public String aParameter = null;

    @Parameter(names = { "--aParameterList"}, description = "A String list.")
    public List<String> aParameterList = null;

    @Test
    public void testParameterHavingQuotes() {
        JCommander jc = new JCommander(this);
        jc.parse("--aParameter=\"X\"");
        Assert.assertNotNull(aParameter);
        // as of JCommander 1.74/75
        Assert.assertEquals(aParameter, "\"X\"", "Expect \"X\" for JCommander 1.74/75 and more recent");
    }

    @Test
    public void testParameterListHavingQuotes() {
        JCommander jc = new JCommander(this);
        jc.parse("--aParameterList=\"X,Y\"");
        Assert.assertNotNull(aParameterList);
        Assert.assertEquals(aParameterList.size(), 2);
        Assert.assertEquals(aParameterList.getFirst(), "\"X");
        Assert.assertEquals(aParameterList.get(1), "Y\"");
    }
}