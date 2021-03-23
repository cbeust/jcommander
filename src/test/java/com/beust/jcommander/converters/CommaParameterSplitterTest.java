package com.beust.jcommander.converters;

import java.util.Arrays;
import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class CommaParameterSplitterTest {

  private static final IParameterSplitter SPLITTER = new CommaParameterSplitter();

  @Test
  public void testSplit() {
    // An empty string becomes an empty list.
    Assert.assertEquals(Collections.emptyList(), SPLITTER.split(""));

    // Whitespace is unaltered.
    Assert.assertEquals(Collections.singletonList("  "), SPLITTER.split("  "));

    // Single value.
    Assert.assertEquals(Collections.singletonList("abc"), SPLITTER.split("abc"));

    // Multiple values.
    Assert.assertEquals(Arrays.asList("a", "b", "c"), SPLITTER.split("a,b,c"));
  }
}
