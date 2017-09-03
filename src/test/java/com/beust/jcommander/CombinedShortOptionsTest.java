package com.beust.jcommander;

import com.beust.jcommander.args.ArgsShort;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class CombinedShortOptionsTest {

    @Test(expectedExceptions = ParameterException.class)
    public void testCombinedShortOptionsDisabled() {
        ArgsShort args = new ArgsShort();
        String[] argv = {"-abc"};
        JCommander.newBuilder().addObject(args).build().parse(argv);
    }

    @Test
    public void testSingleBooleanOption() {
        ArgsShort args = new ArgsShort();
        String[] argv = {"-b"};
        JCommander.newBuilder().addObject(args).allowCombinedShortOptions(true).build().parse(argv);

        Assert.assertFalse(args.a);
        Assert.assertTrue(args.b);
        Assert.assertFalse(args.c);
    }

    @Test
    public void testMultipleBooleanOptions() {
        ArgsShort args = new ArgsShort();
        String[] argv = {"-abc"};
        JCommander.newBuilder().addObject(args).allowCombinedShortOptions(true).build().parse(argv);

        Assert.assertTrue(args.a);
        Assert.assertTrue(args.b);
        Assert.assertTrue(args.c);
    }

    @Test
    public void testStringArgument() {
        ArgsShort args = new ArgsShort();
        String[] argv = {"-acs", "str"};
        JCommander.newBuilder().addObject(args).allowCombinedShortOptions(true).build().parse(argv);

        Assert.assertTrue(args.a);
        Assert.assertFalse(args.b);
        Assert.assertTrue(args.c);
        Assert.assertEquals(args.s, "str");
    }

    @Test
    public void testLongOptionName() {
        ArgsShort args = new ArgsShort();
        String[] argv = {"--longname"};
        JCommander.newBuilder().addObject(args).allowCombinedShortOptions(true).build().parse(argv);

        Assert.assertTrue(args.a);
        Assert.assertFalse(args.b);
        Assert.assertFalse(args.c);
    }

    @Test
    public void testShortOptionsWithDynamicParameters() {
        ArgsShort args = new ArgsShort();
        String[] argv = {"-ab", "-Dparam=str"};
        JCommander.newBuilder().addObject(args).allowCombinedShortOptions(true).build().parse(argv);

        Assert.assertTrue(args.a);
        Assert.assertTrue(args.b);
        Assert.assertFalse(args.c);
        Assert.assertEquals(args.params.get("param"), "str");
    }
}
