package com.beust.jcommander;

import com.beust.jcommander.args.*;
import com.beust.jcommander.internal.Maps;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Test
public class UnixStyleUsageFormatterTest {

    private enum TestEnum1 {
        A, B, C, D
    }

    private enum TestEnum2 {
    }

    @Test
    public void testOutputFormat() {
        class ArgsTemplate {
            @Parameter(names = {"--a", "-a"})
            public int a;
            @Parameter(names = {"--b", "-b"})
            public int b = 2;
            @Parameter(names = {"--c", "-c"}, description = "sets c")
            public int c;
            @Parameter(names = {"--d", "-d"}, description = "sets d")
            public int d = 2;
            @Parameter(names = {"--e"})
            public TestEnum1 e;
            @Parameter(names = {"--f"})
            public TestEnum1 f = TestEnum1.A;
            @Parameter(names = {"--g"}, description = "sets g")
            public TestEnum1 g;
            @Parameter(names = {"--h"}, description = "sets h")
            public TestEnum1 h = TestEnum1.A;
            @Parameter(names = {"-i"})
            public TestEnum2 i;
            @Parameter(names = {"-k"}, description = "sets k")
            public TestEnum2 k;
        }

        // setup
        StringBuilder sb = new StringBuilder();
        JCommander jc = JCommander.newBuilder()
                .addObject(new ArgsTemplate())
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));

        // action
        jc.getUsageFormatter().usage(sb);

        // verify
        String expected = "Usage: <main class> [options]\n"
                + "  Options:\n"
                + "    --a, -a (default: 0)\n"
                + "    --b, -b (default: 2)\n"
                + "    --c, -c sets c (default: 0)\n"
                + "    --d, -d sets d (default: 2)\n"
                + "    --e     Options: [A, B, C, D]\n"
                + "    --f     Options: [A, B, C, D] (default: A)\n"
                + "    --g     sets g (values: [A, B, C, D])\n"
                + "    --h     sets h (default: A) (values: [A, B, C, D])\n"
                + "    -i      Options: []\n"
                + "    -k      sets k (values: [])\n";
        Assert.assertEquals(sb.toString(), expected);
    }

    @Test
    public void testLongMainParameterDescription() {
        //setup
        JCommander jc = JCommander.newBuilder()
                .addObject(new ArgsLongMainParameterDescription())
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        StringBuilder sb = new StringBuilder();

        //action
        jc.getUsageFormatter().usage(sb);

        //verify
        for (String line : sb.toString().split("\n")) {
            Assert.assertTrue(line.length() <= jc.getColumnSize(), "line length < column size");
        }
    }

    @Test
    public void testLongCommandDescription() throws Exception {
        //setup
        JCommander jc = JCommander.newBuilder()
                .addCommand(new ArgsLongCommandDescription())
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        StringBuilder sb = new StringBuilder();

        //action
        jc.getUsageFormatter().usage(sb);

        //verify
        for (String line : sb.toString().split("\n")) {
            Assert.assertTrue(line.length() <= jc.getColumnSize(), "line length < column size");
        }
    }

    @Test
    public void testDescriptionWrappingLongWord() {
        //setup
        StringBuilder sb = new StringBuilder();
        final JCommander jc = JCommander.newBuilder()
                .addObject(new ArgsLongDescription())
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));

        //action
        jc.getUsageFormatter().usage(sb);

        //verify
        for (String line : sb.toString().split("\n")) {
            Assert.assertTrue(line.length() <= jc.getColumnSize(), "line length < column size");
        }
    }

    @Test
    public void programName() {
        JCommander jcommander = new JCommander();
        jcommander.setUsageFormatter(new UnixStyleUsageFormatter(jcommander));
        String programName = "main";
        jcommander.setProgramName(programName);
        StringBuilder sb = new StringBuilder();
        jcommander.getUsageFormatter().usage(sb);

        Assert.assertTrue(sb.toString().contains(programName));
        Assert.assertEquals(jcommander.getProgramName(), programName);
    }

    @Test
    public void dontShowOptionUsageIfThereAreNoOptions() {
        class CommandTemplate {
            @Parameter
            List<String> parameters = new ArrayList<>();
        }

        CommandTemplate template = new CommandTemplate();
        JCommander jcommander = JCommander.newBuilder()
                .addObject(template)
                .build();
        jcommander.setUsageFormatter(new UnixStyleUsageFormatter(jcommander));
        jcommander.setProgramName("main");
        StringBuilder sb = new StringBuilder();
        jcommander.getUsageFormatter().usage(sb);
        Assert.assertEquals(sb.toString().indexOf("options"), -1);
    }

    @Test
    public void annotationsAndDynamicParameters() {
        class DSimple {
            @DynamicParameter(names = "-D", description = "Dynamic parameters go here")
            public Map<String, String> params = Maps.newHashMap();

            @DynamicParameter(names = "-A", assignment = "@")
            public Map<String, String> params2 = Maps.newHashMap();
        }

        JCommander jc = JCommander.newBuilder()
                .addObject(new DSimple())
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        jc.getUsageFormatter().usage(new StringBuilder());
    }

    /**
     * Getting the description of a nonexistent command should throw an exception.
     */
    @Test(expectedExceptions = ParameterException.class)
    public void nonexistentCommandShouldThrow() {
        String[] argv = {};
        JCommander jc = JCommander.newBuilder().addObject(new Object()).build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        jc.parse(argv);
        jc.getUsageFormatter().getCommandDescription("foo");
    }

    @Test
    public void i18MissingKeyForCommand() {
        JCommander jc = JCommander.newBuilder()
                .addObject(new ArgsHelp())
                .resourceBundle(DefaultUsageFormatterTest.getResourceBundle())
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        jc.addCommand(new ArgsLongCommandDescription());
        StringBuilder sb = new StringBuilder();
        jc.getUsageFormatter().usage(sb);
        String usage = sb.toString();
        Assert.assertTrue(usage.contains("text"));
    }

    @Test
    public void noParseConstructor() {
        JCommander jCommander = JCommander.newBuilder()
                .addObject(new ArgsMainParameter1())
                .build();
        jCommander.setUsageFormatter(new UnixStyleUsageFormatter(jCommander));
        jCommander.getUsageFormatter().usage(new StringBuilder());
        // Before fix, this parse would throw an exception, because it calls createDescription, which
        // was already called by usage(), and can only be called once.
        jCommander.parse();
    }

    /**
     * Test a use case where there are required parameters, but you still want
     * to interrogate the options which are specified.
     */
    @Test
    public void usageWithRequiredArgsAndResourceBundle() {
        ArgsHelp argsHelp = new ArgsHelp();
        JCommander jc = JCommander.newBuilder()
                .addObject(new Object[] {argsHelp, new ArgsRequired()})
                .resourceBundle(DefaultUsageFormatterTest.getResourceBundle())
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        // Should be able to display usage without triggering validation
        jc.getUsageFormatter().usage(new StringBuilder());
        try {
            jc.parse("-h");
            Assert.fail("Should have thrown a required parameter exception");
        } catch (ParameterException e) {
            Assert.assertTrue(e.getMessage().contains("are required"));
        }
        Assert.assertTrue(argsHelp.help);
    }

    @Test
    public void usageShouldNotChange() {
        JCommander jc = JCommander.newBuilder().addObject(new Args1()).build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        jc.parse("-log", "1");
        StringBuilder sb = new StringBuilder();
        jc.getUsageFormatter().usage(sb);
        String expected = sb.toString();

        sb = new StringBuilder();
        jc.getUsageFormatter().usage(sb);
        String actual = sb.toString();
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "This used to run out of memory")
    public void oom() {
        JCommander jc = JCommander.newBuilder()
                .addObject(new ArgsOutOfMemory())
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        jc.getUsageFormatter().usage(new StringBuilder());
    }

    @Test
    public void doNotDisplayHelpDefaultValue() {
        class Arg {
            @Parameter(names = "--help", help = true)
            public boolean help = false;
        }
        Arg args = new Arg();
        String[] argv = {"--help"};
        JCommander jc = JCommander.newBuilder().addObject(args).build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        jc.parse(argv);

        StringBuilder sb = new StringBuilder();

        jc.getUsageFormatter().usage(sb);

        Assert.assertFalse(sb.toString().contains("Default"));
    }

    @Test
    public void usageCommandsUnderUsage() {
        class Arg {
        }
        @Parameters(commandDescription = "command a")
        class ArgCommandA {
            @Parameter(description = "command a parameters")
            List<String> parameters;
        }
        @Parameters(commandDescription = "command b")
        class ArgCommandB {
            @Parameter(description = "command b parameters")
            List<String> parameters;
        }

        Arg a = new Arg();

        JCommander jc = JCommander.newBuilder()
                .addObject(a)
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        jc.addCommand("a", new ArgCommandA());
        jc.addCommand("b", new ArgCommandB());

        StringBuilder sb = new StringBuilder();
        jc.getUsageFormatter().usage(sb);
        Assert.assertTrue(sb.toString().contains("[command options]\n\n  Commands:"));
    }

    @Test
    public void usageWithEmptyLine() {
        class Arg {
        }
        @Parameters(commandDescription = "command a")
        class ArgCommandA {
            @Parameter(description = "command a parameters")
            List<String> parameters;
        }
        @Parameters(commandDescription = "command b")
        class ArgCommandB {
            @Parameter(description = "command b parameters")
            List<String> parameters;
        }

        Arg a = new Arg();

        JCommander jc = JCommander.newBuilder()
                .addObject(a)
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        jc.addCommand("a", new ArgCommandA());
        jc.addCommand("b", new ArgCommandB());

        StringBuilder sb = new StringBuilder();
        jc.getUsageFormatter().usage(sb);
        String expected = "Usage: <main class> [command] [command options]\n"
                + "\n"
                + "  Commands:\n"
                + "    a      command a\n"
                + "      Usage: a command a parameters\n"
                + "\n"
                + "    b      command b\n"
                + "      Usage: b command b parameters\n";
        Assert.assertEquals(sb.toString(), expected);
    }

    @Test
    public void usageWithSubCommands() {
        class Arg {
        }
        @Parameters(commandDescription = "command a")
        class ArgCommandA {
            @Parameter(description = "command a parameters")
            List<String> parameters;
        }
        @Parameters(commandDescription = "command b")
        class ArgCommandB {
            @Parameter(description = "command b parameters")
            List<String> parameters;
        }

        Arg a = new Arg();

        JCommander jc = JCommander.newBuilder()
                .addObject(a)
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        jc.setColumnSize(100);
        jc.addCommand("a", new ArgCommandA());

        // b is a sub-command of a
        JCommander aCommand = jc.getCommands().get("a");
        aCommand.addCommand("b", new ArgCommandB());

        StringBuilder sb = new StringBuilder();
        jc.getUsageFormatter().usage(sb);
        Assert.assertTrue(sb.toString().contains("command a parameters\n\n        Commands:"));
        Assert.assertTrue(sb.toString().contains("command b\n            Usage:"));
    }

    @Test
    public void emptyStringAsDefault() {
        class Arg {
            @Parameter(names = "-x")
            String s = "";
        }
        Arg a = new Arg();
        StringBuilder sb = new StringBuilder();
        JCommander jc = JCommander.newBuilder()
                .addObject(a)
                .build();
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
        jc.getUsageFormatter().usage(sb);
        Assert.assertTrue(sb.toString().contains("default: <empty string>"));
    }
}
