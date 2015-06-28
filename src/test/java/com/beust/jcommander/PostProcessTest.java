package com.beust.jcommander;

import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class PostProcessTest {
  private class OneParameter {
    @Parameter(names = "--foo")
    public boolean foo;
  }

  final AtomicBoolean processed = new AtomicBoolean(false);

  @BeforeMethod
  public void reset() {
    processed.set(false);
  }

  @Test
  public void testPostProcessClean() {
    OneParameter op = new OneParameter() {
      @PostProcess
      public void pp() {
        processed.set(true);
      }
    };

    new JCommander(op).parse("--foo");
    Assert.assertEquals(processed.get(), true);
  }

  @Test(expectedExceptions = ParameterException.class)
  public void testPostProcessException() {
    OneParameter op = new OneParameter() {
      @PostProcess
      public void pp() {
        throw new IllegalStateException();
      }
    };

    new JCommander(op).parse("--foo");
  }

  @Test
  public void testPostProcessReturnTrue() {
    OneParameter op = new OneParameter() {
      @PostProcess
      public boolean pp() {
        processed.set(true);
        return true;
      }
    };

    new JCommander(op).parse("--foo");
    Assert.assertEquals(processed.get(), true);
  }

  @Test(expectedExceptions = ParameterException.class)
  public void testPostProcessReturnFalse() {
    OneParameter op = new OneParameter() {
      @PostProcess
      public boolean pp() {
        processed.set(true);
        return false;
      }
    };

    new JCommander(op).parse("--foo");
  }

  @Test(expectedExceptions = ParameterException.class, expectedExceptionsMessageRegExp = "Lorem Ipsum")
  public void testPostProcessReturnString() {
    OneParameter op = new OneParameter() {
      @PostProcess
      public String pp() {
        processed.set(true);
        return "Lorem Ipsum";
      }
    };

    new JCommander(op).parse("--foo");
  }
}
