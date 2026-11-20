package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class Issue575ValidatorScopeTest {

    @Parameters(parametersValidators = MyValidator.class)
    static class SubGroup {
        @Parameter(names = "--arg1")
        private boolean arg1;

        @Parameter(names = "--arg2")
        private String arg2;

        public boolean isArg1() {
            return arg1;
        }

        public String getArg2() {
            return arg2;
        }
    }

    static class ArgsTop {
        @Parameter(names = "--argtop")
        private boolean argtop;

        @ParametersDelegate
        private SubGroup subGroup = new SubGroup();

        public boolean isArgtop() {
            return argtop;
        }
    }

    static class MyValidator implements IParametersValidator {
        @Override
        public void validate(Map<String, Object> params) throws ParameterException {
            if (params.containsKey("--argtop")) {
                throw new ParameterException("Validator should not see --argtop, but found it in: " + params.keySet());
            }
            if (!params.containsKey("--arg1") || !params.containsKey("--arg2")) {
                throw new ParameterException("Validator should see --arg1 and --arg2, but found: " + params.keySet());
            }
        }
    }

    // Pre-fix: Expect exception due to bug (passes with original JCommander)
    @Test(expectedExceptions = ParameterException.class, expectedExceptionsMessageRegExp = ".*Validator should not see --argtop.*")
    public void testValidatorScopeBug() {
        ArgsTop args = new ArgsTop();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(args)
                .build();
        jCommander.parse("--argtop", "--arg1", "--arg2", "value");
        // These won’t run pre-fix due to exception
        Assert.assertTrue(args.isArgtop(), "--argtop should be set");
        Assert.assertTrue(args.subGroup.isArg1(), "--arg1 should be set");
        Assert.assertEquals(args.subGroup.getArg2(), "value", "--arg2 should be set");
    }

    // Post-fix: Expect no exception, verify parameters (passes with fixed JCommander)
    @Test
    public void testValidatorScopeFixed() {
        ArgsTop args = new ArgsTop();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(args)
                .build();
        jCommander.parse("--argtop", "--arg1", "--arg2", "value");
        Assert.assertTrue(args.isArgtop(), "--argtop should be set");
        Assert.assertTrue(args.subGroup.isArg1(), "--arg1 should be set");
        Assert.assertEquals(args.subGroup.getArg2(), "value", "--arg2 should be set");
    }
}