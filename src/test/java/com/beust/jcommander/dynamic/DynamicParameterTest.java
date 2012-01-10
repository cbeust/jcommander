package com.beust.jcommander.dynamic;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Maps;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class DynamicParameterTest {

  @Test
  public void simple() {
    DSimple ds = new DSimple();
    new JCommander(ds).parse("-Da=b", "-Dc=d");
    Map<String, String> expected = Maps.newHashMap("a", "b", "c", "d");
    Assert.assertEquals(ds.params, expected);
  }
}
