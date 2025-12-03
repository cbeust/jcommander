package com.beust.jcommander.converters;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class CommaParameterSplitterTest {

  private static final IParameterSplitter SPLITTER = new CommaParameterSplitter();

  @Test
  public void testSplit() {
    // An empty string becomes an empty list.
    Assert.assertEquals(List.of(), SPLITTER.split(""));

    // Whitespace is unaltered.
    Assert.assertEquals(List.of("  "), SPLITTER.split("  "));

    // Single value.
    Assert.assertEquals(List.of("abc"), SPLITTER.split("abc"));

    // Multiple values.
    Assert.assertEquals(List.of("a", "b", "c"), SPLITTER.split("a,b,c"));
  }
}
