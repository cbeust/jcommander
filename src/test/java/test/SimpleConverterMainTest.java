package test;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SimpleConverterMainTest {

    @Test
    public void testConstructorMain() {
        ConstructorArgs args = new ConstructorArgs();

        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse("-test", "test");

        Assert.assertEquals(args.test.getClass(), ConstructorConverter.class);
        Assert.assertEquals(args.test.getValue(), "test");
    }

    @Test
    public void testNonPublicConstructorMain() {
        NonPublicConstructorArgs args = new NonPublicConstructorArgs();

        try {
            JCommander.newBuilder()
                    .addObject(args)
                    .build()
                    .parse("-test", "test");
            Assert.fail("ParameterException expected");
        }
        catch(ParameterException e) {
            Assert.assertTrue(e.getMessage().startsWith("Can't find any implicit converter mechanism for option '-test'"));
        }
    }

    @Test
    public void testValueOfMain() {
        ValueOfArgs args = new ValueOfArgs();

        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse("-test", "test");

        Assert.assertEquals(args.test.getClass(), ValueOfConverter.class);
        Assert.assertEquals(args.test.getValue(), "test");
    }

    @Test
    public void testNonPublicValueOfMain() {
        NonPublicValueOfArgs args = new NonPublicValueOfArgs();

        try {
            JCommander.newBuilder()
                    .addObject(args)
                    .build()
                    .parse("-test", "test");
            Assert.fail("ParameterException expected");
        }
        catch(ParameterException e) {
            Assert.assertTrue(e.getMessage().startsWith("Can't find any implicit converter mechanism for option '-test'"));
        }
    }

    @Test
    public void testWrongTypeValueOfMain() {
        WrongTypeValueOfArgs args = new WrongTypeValueOfArgs();

        try {
            JCommander.newBuilder()
                    .addObject(args)
                    .build()
                    .parse("-test", "test");
        }
        catch(ParameterException e) {
            Assert.assertTrue(e.getMessage().startsWith("The converting valueOf method for option '-test' returns type"));
        }
    }

    @Test
    public void testValueOfBeforeConstructorMain() {
        ValueOfBeforeConstructorArgs args = new ValueOfBeforeConstructorArgs();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse("-test", "test");

        Assert.assertEquals(args.test.getClass(), BothConverter.class);
        Assert.assertEquals(args.test.getValue(), "valueOf-test");
    }

    public static void main(String[] args) {
        new SimpleConverterMainTest().testConstructorMain();
        new SimpleConverterMainTest().testNonPublicConstructorMain();
        new SimpleConverterMainTest().testValueOfMain();
        new SimpleConverterMainTest().testNonPublicValueOfMain();
        new SimpleConverterMainTest().testWrongTypeValueOfMain();
        new SimpleConverterMainTest().testValueOfBeforeConstructorMain();
    }

    public static class ConstructorArgs {
        @Parameter(names = "-test", required = true)
        public ConstructorConverter test;
    }

    public static class NonPublicConstructorArgs {
        @Parameter(names = "-test", required = true)
        public NonPublicConstructorConverter test;
    }

    public static class ValueOfArgs {
        @Parameter(names = "-test", required = true)
        public ValueOfConverter test;
    }

    public static class NonPublicValueOfArgs {
        @Parameter(names = "-test", required = true)
        public NonPublicValueOfConverter test;
    }

    public static class WrongTypeValueOfArgs {
        @Parameter(names = "-test", required = true)
        public WrongTypeValueOfCreator test;
    }

    public static class ValueOfBeforeConstructorArgs {
        @Parameter(names = "-test", required = true)
        public BothConverter test;
    }

    public static class ConstructorConverter {
        private final String mValue;

        public ConstructorConverter(String aValue) {
            mValue = aValue;
        }

        public String getValue() {
            return mValue;
        }
    }

    public static class NonPublicConstructorConverter {
        private final String mValue;

        protected NonPublicConstructorConverter(String aValue) {
            mValue = aValue;
        }

        public String getValue() {
            return mValue;
        }
    }


    public static class ValueOfConverter {
        private final String mValue;

        private ValueOfConverter(String aValue) {
            mValue = aValue;
        }

        public String getValue() {
            return mValue;
        }

        public static ValueOfConverter valueOf(String aValue) {
            return new ValueOfConverter(aValue);
        }
    }

    public static class NonPublicValueOfConverter {
        private final String mValue;

        private NonPublicValueOfConverter(String aValue) {
            mValue = aValue;
        }

        public String getValue() {
            return mValue;
        }

        protected static NonPublicValueOfConverter valueOf(String aValue) {
            return new NonPublicValueOfConverter(aValue);
        }
    }

    public static class WrongTypeValueOfCreator {
        public static Object valueOf(String aValue) {
            return null;
        }
    }

    public static class BothConverter {
        private final String mValue;

        public BothConverter(String aValue) {
            mValue = aValue;
        }

        public String getValue() {
            return mValue;
        }

        public static BothConverter valueOf(String aValue) {
            return new BothConverter("valueOf-" + aValue);
        }
    }
}
