package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Tests for @Parameter on top of methods.
 */
@Test
public class MethodSetterTest {
  public void arityStringsSetter() {
    class ArgsArityStringSetter {

      @Parameter(names = "-pairs", arity = 2, description = "Pairs")
      public void setPairs(List<String> pairs) {
        this.pairs = pairs;
      }
      public List<String> getPairs() {
        return this.pairs;
      }
      public List<String> pairs;

      @Parameter(description = "Rest")
      public void setRest(List<String> rest) {
        this.rest = rest;
      }
//      public List<String> getRest() {
//        return this.rest;
//      }
      public List<String> rest;
    }
    ArgsArityStringSetter args = new ArgsArityStringSetter();
    String[] argv = { "-pairs", "pair0", "pair1", "rest" };
    new JCommander(args, argv);

    Assert.assertEquals(args.pairs.size(), 2);
    Assert.assertEquals(args.pairs.get(0), "pair0");
    Assert.assertEquals(args.pairs.get(1), "pair1");
    Assert.assertEquals(args.rest.size(), 1);
    Assert.assertEquals(args.rest.get(0), "rest");
  }

  public void setterThatThrows() {
    class Arg {
      @Parameter(names = "--host")
      public void setHost(String host) {
        throw new ParameterException("Illegal host");
      }
    }
    boolean passed = false;
    try {
      new JCommander(new Arg(), new String[] { "--host", "host" });
    } catch(ParameterException ex) {
      Assert.assertEquals(ex.getCause(), null);
      passed = true;
    }
    Assert.assertTrue(passed, "Should have thrown an exception");
  }

  @Test(enabled = false)
  public static void main(String[] args) throws Exception {
    new MethodSetterTest().arityStringsSetter();
  }
}
