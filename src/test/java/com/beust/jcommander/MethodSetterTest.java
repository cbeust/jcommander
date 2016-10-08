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
      new JCommander(new Arg(), "--host", "host");
    } catch(ParameterException ex) {
      Assert.assertEquals(ex.getCause(), null);
      passed = true;
    }
    Assert.assertTrue(passed, "Should have thrown an exception");
  }

  public void getterReturningNonString() {
    class Arg {
      private Integer port;

      @Parameter(names = "--port")
      public void setPort(String port) {
        this.port = Integer.parseInt(port);
      }

      public Integer getPort() {
        return port;
      }
    }
    Arg arg = new Arg();
    new JCommander(arg, "--port", "42");

    Assert.assertEquals(arg.port, new Integer(42));
  }

  public void noGetterButWithField() {
    class Arg {
      private Integer port = 43;

      @Parameter(names = "--port")
      public void setPort(String port) {
        this.port = Integer.parseInt(port);
      }
    }
    Arg arg = new Arg();
    JCommander jc = new JCommander(arg, "--port", "42");
    ParameterDescription pd = jc.getParameters().get(0);
    Assert.assertEquals(pd.getDefault(), 43);
  }

  @Test(enabled = false)
  public static void main(String[] args) throws Exception {
    new MethodSetterTest().noGetterButWithField();
  }
}
