package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CmdTest {

    @Parameters(commandNames = "--cmd-one")
    public static class CmdOne {
    }

    @Parameters(commandNames = "--cmd-two")
    class CmdTwo {
        @Parameter
        List<String> params = new java.util.LinkedList<>();
    }

    public String parseArgs(boolean withDefault, String[] args) {
        JCommander jc = new JCommander();
        jc.addCommand(new CmdOne());
        jc.addCommand(new CmdTwo());

        if (withDefault) {
            // First check if a command was given, when not prepend default
            // command (--cmd-two")
            // In version up to 1.23 JCommander throws an Exception in this
            // line,
            // which might be incorrect, at least its not reasonable if the
            // method
            // is named "WithoutValidation".
            jc.parseWithoutValidation(args);
            if (jc.getParsedCommand() == null) {
                LinkedList<String> newArgs = new LinkedList<>();
                newArgs.add("--cmd-two");
                newArgs.addAll(Arrays.asList(args));
                jc.parse(newArgs.toArray(new String[0]));
            }
        } else {
            jc.parse(args);
        }
        return jc.getParsedCommand();
    }

    @DataProvider
    public Object[][] testData() {
        return new Object[][] {
                new Object[] { "--cmd-one", false, new String[] { "--cmd-one" } },
                new Object[] { "--cmd-two", false, new String[] { "--cmd-two" } },
                new Object[] { "--cmd-two", false,
                        new String[] { "--cmd-two", "param1", "param2" } },
                // This is the relevant test case to test default commands
                new Object[] { "--cmd-two", true,
                        new String[] { "param1", "param2" } } };
    }

    @Test(dataProvider = "testData")
    public void testArgsWithoutDefaultCmd(String expected,
            boolean requireDefault, String[] args) {
        if (!requireDefault) {
            Assert.assertEquals(parseArgs(false, args), expected);
        }
    }

    @Test(dataProvider = "testData", expectedExceptions = MissingCommandException.class)
    public void testArgsWithoutDefaultCmdFail(String expected,
            boolean requireDefault, String[] args) {
        if (requireDefault) {
            try {
                parseArgs(false, args);
            } catch (MissingCommandException e) {
                Assert.assertEquals(e.getUnknownCommand(), args[0]);
                throw e;
            }
        } else {
            throw new MissingCommandException("irrelevant test case");
        }
    }

    // We do not expect a MissingCommandException!
    @Test(dataProvider = "testData")
    public void testArgsWithDefaultCmd(String expected, boolean requireDefault,
            String[] args) {
        Assert.assertEquals(parseArgs(true, args), expected);
    }

    @Test
    public void testIssue244() throws Exception {
        class P1 {}
        class P2 {
            @Parameter(names = "--hello")
            private int test;
        }
        P1 p1 = new P1();
        P2 p2 = new P2();
        JCommander j = new JCommander(p1);
        j.addCommand("wonderful", p2);
        j.setAllowAbbreviatedOptions(true);
        j.parse("wond", "--he", "47");
        Assert.assertEquals("wonderful", j.getParsedCommand());
        Assert.assertEquals(47, p2.test);
    }
}
