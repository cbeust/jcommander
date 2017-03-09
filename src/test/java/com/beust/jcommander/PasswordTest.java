package com.beust.jcommander;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
    public void passwordNotAsked(Args a) {
        String expectedPassword = "somepassword";
        int expectedPort = 7;
        new JCommander(a, "--password", expectedPassword, "--port", String.valueOf(7));
        Assert.assertEquals(a.getPort(), expectedPort);
        Assert.assertEquals(a.getPassword(), expectedPassword);
    }

    @Test(dataProvider = "args", expectedExceptions = ParameterException.class)
    public void passwordWithExcessiveArity(Args a) {
        new JCommander(a, "--password", "somepassword", "someotherarg", "--port", String.valueOf(7));
    }

    @Test(dataProvider = "args")
    public void passwordAsked(Args a) {
        InputStream stdin = System.in;
        String password = "password";
        int port = 7;
        try {
            System.setIn(new ByteArrayInputStream(password.getBytes()));
            new JCommander(a, "--port", String.valueOf(port), "--password");
            Assert.assertEquals(a.getPort(), port);
            Assert.assertEquals(a.getPassword(), password);
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
        Args a = new OptionalPasswordTestingArgs();
        new JCommander(a, "--port", "7");
        Assert.assertEquals(a.getPort(), 7);
        Assert.assertEquals(a.getPassword(), null);
    }

    @Test(expectedExceptions = ParameterException.class)
    public void passwordRequredNotProvided() {
        Args a = new PasswordTestingArgs();
        new JCommander(a, "--port", "7");
    }

}
