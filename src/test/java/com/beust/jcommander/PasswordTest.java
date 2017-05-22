package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class PasswordTest {

    @DataProvider(name = "args")
    public Object[][] createArgs() {
        return new Object[][] {
                { new PasswordTestingArgs() },
                { new OptionalPasswordTestingArgs() },
        };
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

}
