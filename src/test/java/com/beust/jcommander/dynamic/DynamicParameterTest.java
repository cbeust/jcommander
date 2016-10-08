package com.beust.jcommander.dynamic;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.internal.Maps;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class DynamicParameterTest {

  @Test(expectedExceptions = ParameterException.class)
  public void nonMapShouldThrow() {
    new JCommander(new DSimpleBad()).parse("-D", "a=b", "-D", "c=d");
  }

  @Test(expectedExceptions = ParameterException.class)
  public void wrongSeparatorShouldThrow() {
    DSimple ds = new DSimple();
    new JCommander(ds).parse("-D", "a:b", "-D", "c=d");
  }

  private void simple(String... parameters) {
    DSimple ds = new DSimple();
    new JCommander(ds).parse(parameters);
    Assert.assertEquals(ds.params, Maps.newHashMap("a", "b", "c", "d"));
  }

  public void simpleWithSpaces() {
    simple("-D", "a=b", "-D", "c=d");
  }

  public void simpleWithoutSpaces() {
    simple("-Da=b", "-Dc=d");
  }

  public void usage() {
    DSimple ds = new DSimple();
    new JCommander(ds).usage(new StringBuilder());
  }

  public void differentAssignment() {
    DSimple ds = new DSimple();
    new JCommander(ds).parse("-D", "a=b", "-A", "c@d");
    Assert.assertEquals(ds.params, Maps.newHashMap("a", "b"));
    Assert.assertEquals(ds.params2, Maps.newHashMap("c", "d"));
  }

  @Test(enabled = false)
  public static void main(String[] args) {
    DynamicParameterTest dpt = new DynamicParameterTest();
    dpt.simpleWithSpaces();
  }
}
