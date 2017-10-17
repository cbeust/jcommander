package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

@Test
public class StringsTest {

    @Test
    public void testListJoin() {
        String expected = "A, B, C  c";
        String actual = Strings.join(", ", Arrays.asList("A", "B", "C  c"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testArrayJoinSpaceDelimiter() {
        String expected = "A B C  c";
        String actual = Strings.join(" ", new String[] { "A", "B", "C  c" });
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testArrayJoinEmptyDelimiter() {
        String expected = "ABC  c";
        String actual = Strings.join("", new Object[] { "A", "B", "C  c" });
        Assert.assertEquals(expected, actual);
    }
}
