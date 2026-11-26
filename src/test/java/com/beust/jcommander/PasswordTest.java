package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class PasswordTest {

    @DataProvider(name = "args")
    public Object[][] createArgs() {
        return new Object[][] {
                { new PasswordTestingArgs() },
                { new OptionalPasswordTestingArgs() },
        };
    }

    @DataProvider(name = "acceptUnknownOptions")
    public Object[][] acceptUnknownOptions() {
        return new Object[][] { { false }, { true } };
    }

    public interface Args {

        String getPassword();

        int getPort();

    }

    public static class PasswordTestingArgs implements PasswordTest.Args {
        @Parameter(names = {"--password", "-p"}, description = "Private key password",
                password = true, required = true)
        public String password;

        @Parameter(names = {"--port", "-o"}, description = "Port to bind server to",
                required = true)
        public int port;

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public int getPort() {
            return port;
        }
    }

    @Test(dataProvider = "args")
    public void passwordNotAsked(Args args) {
        String expectedPassword = "somepassword";
        int expectedPort = 7;
        JCommander.newBuilder().addObject(args).build()
                .parse("--password", expectedPassword, "--port", String.valueOf(7));
        Assert.assertEquals(args.getPort(), expectedPort);
        Assert.assertEquals(args.getPassword(), expectedPassword);
    }

    @Test(dataProvider = "args", expectedExceptions = ParameterException.class)
    public void passwordWithExcessiveArity(Args args) {
        JCommander.newBuilder().addObject(args).build()
                .parse("--password", "somepassword", "someotherarg", "--port", String.valueOf(7));
    }

    @Test(dataProvider = "args")
    public void passwordAsked(Args args) {
        InputStream stdin = System.in;
        String password = "password";
        int port = 7;
        try {
            System.setIn(new ByteArrayInputStream(password.getBytes()));
            JCommander.newBuilder().addObject(args).build().parse("--port", String.valueOf(port), "--password");
            Assert.assertEquals(args.getPort(), port);
            Assert.assertEquals(args.getPassword(), password);
        } finally {
            System.setIn(stdin);
        }
    }

    public static class OptionalPasswordTestingArgs implements PasswordTest.Args {
        @Parameter(names = {"--password", "-p"}, description = "Private key password",
                password = true)
        public String password;

        @Parameter(names = {"--port", "-o"}, description = "Port to bind server to",
                required = true)
        public int port;

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public int getPort() {
            return port;
        }
    }

    @Test
    public void passwordOptionalNotProvided() {
        Args args = new OptionalPasswordTestingArgs();
        JCommander.newBuilder().addObject(args).build().parse("--port", "7");
        Assert.assertEquals(args.getPort(), 7);
        Assert.assertEquals(args.getPassword(), null);
    }

    @Test(expectedExceptions = ParameterException.class)
    public void passwordRequredNotProvided() {
        Args args = new PasswordTestingArgs();
        JCommander.newBuilder().addObject(args).build().parse("--port", "7");
    }

    @Test(dataProvider = "args")
    public void passwordWithUnknownOptionsAndKnownOptionSeparator(Args args) {
        StringBuilder output = new StringBuilder();
        JCommander commander = JCommander.newBuilder()
                .addObject(args)
                .console(new StringBuilderConsole(output))
                .acceptUnknownOptions(true)
                .build();
        commander.parse("--password", "synthetic-value", "--port", "7", "--unrecognized", "external-value");

        Assert.assertEquals(args.getPassword(), "synthetic-value");
        Assert.assertEquals(args.getPort(), 7);
        Assert.assertEquals(commander.getUnknownOptions(), List.of("--unrecognized", "external-value"));
    }

    public static class PasswordAndPortArgs {
        @Parameter(names = "first.password", password = true, required = true)
        public String firstPassword;

        @Parameter(names = "second.password", password = true, hidden = true, required = true)
        public String secondPassword;

        @Parameter(names = "--port")
        public int port;
    }

    @Test(dataProvider = "acceptUnknownOptions")
    public void multiplePasswordFieldsWithoutLeadingDash(boolean acceptUnknownOptions) {
        PasswordAndPortArgs args = new PasswordAndPortArgs();
        StringBuilder output = new StringBuilder();
        JCommander commander = JCommander.newBuilder()
                .addObject(args)
                .console(new StringBuilderConsole(output))
                .acceptUnknownOptions(acceptUnknownOptions)
                .build();
        commander.parse("first.password", "secretvalue1", "second.password", "secretvalue2", "--port", "8080");

        Assert.assertEquals(args.firstPassword, "secretvalue1");
        Assert.assertEquals(args.secondPassword, "secretvalue2");
        Assert.assertEquals(args.port, 8080);
        Assert.assertEquals(commander.getUnknownOptions(), List.of());
    }

}
