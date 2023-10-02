package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class SetOfEnumTest {

    public enum Season{
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER;
    }

    @Test
    public void testParse()
    {
        class Args {
            @Parameter(names = { "--season"}, description = "List of seasons separated by comma")
            private Set<Season> seasons = new HashSet<>();
        }
        Args args = new Args();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse("--season", "SPRING,AUTUMN");
        Assert.assertEquals(EnumSet.copyOf(Arrays.asList(Season.SPRING, Season.AUTUMN)), args.seasons);
    }

    public static void main(String[] args) {
        new SetOfEnumTest().testParse();
    }
}
