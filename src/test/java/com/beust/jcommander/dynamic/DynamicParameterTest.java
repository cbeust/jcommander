package com.beust.jcommander.dynamic;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.internal.Maps;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class DynamicParameterTest {

  // test using the wrong separator
  @Test(expectedExceptions = ParameterException.class)
  public void nonMapShouldThrow() {
    new JCommander(new DSimpleBad()).parse("-D", "a=b", "-D", "c=d");
  }

  @Test
  public void wrongSeparatorShouldThrow() {
    DSimple ds = new DSimple();
    new JCommander(ds).parse("-D", "a:b", "-D", "c=d");
  }

  @Test
  public void simple() {
    DSimple ds = new DSimple();
    new JCommander(ds).parse("-D", "a=b", "-D", "c=d");
    Map<String, String> expected = Maps.newHashMap("a", "b", "c", "d");
    Assert.assertEquals(ds.params, expected);
  }

  public static void main(String[] args) {
    DynamicParameterTest dpt = new DynamicParameterTest();
//    new DynamicParameterTest().simple();
//    dpt.nonMapShouldThrow();
    dpt.wrongSeparatorShouldThrow();
  }
}
