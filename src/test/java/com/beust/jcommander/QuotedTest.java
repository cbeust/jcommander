package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class QuotedTest {

    public class QuotedArgs {
        @Parameter(names = {"-ql", "--quotedList"}, description = "An argument list with quotes.")
        List<String> ql = new ArrayList<>();

        @Parameter(names = {"-q", "--quoted"}, description = "An argument with quotes.")
        String q;
    }

    private QuotedArgs args;

    @BeforeTest
    public void initTest() {
        this.args = new QuotedArgs();
    }

    @AfterTest
    public void destroyTest() {
        this.args = null;
    }

    @Test
    public void testQuotedArgumentList() {
        JCommander
            .newBuilder()
            .addObject(this.args)
            .build()
            .parse("-ql", "\"foo\",bar");

        Assert.assertEquals(args.ql.size(), 2);
        Assert.assertEquals(args.ql.get(0), "\"foo\"");
        Assert.assertEquals(args.ql.get(1), "bar");
    }

    @Test
    public void testQuotedArgument() {
        JCommander
            .newBuilder()
            .addObject(this.args)
            .build()
            .parse("-q", "\"foo\"");

        Assert.assertEquals(args.q, "\"foo\"");
    }

    @Test
    public void testStupidSpace(){
        JCommander
                .newBuilder()
                .addObject(this.args)
                .build()
                .parse("-q", "\"foo\"");

        Assert.assertEquals(args.q, "\"foo\"");
    }
}
