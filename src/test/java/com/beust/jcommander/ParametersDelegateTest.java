package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rodionmoiseev
 */
public class ParametersDelegateTest {

  @Test
  public void delegatingEmptyClassHasNoEffect() {
    class EmptyDelegate {
      public String nonParamString = "a";
    }
    class MainParams {
      @Parameter(names = "-a")
      public boolean isA;
      @Parameter(names = {"-b", "--long-b"})
      public String bValue = "";
      @ParametersDelegate
      public EmptyDelegate delegate = new EmptyDelegate();
    }

    MainParams p = new MainParams();
    JCommander cmd = new JCommander(p);
    cmd.parse("-a", "-b", "someValue");
    Assert.assertTrue(p.isA);
    Assert.assertEquals(p.bValue, "someValue");
    Assert.assertEquals(p.delegate.nonParamString, "a");
  }

  @Test
  public void delegatingSetsFieldsOnBothMainParamsAndTheDelegatedParams() {
    class ComplexDelegate {
      @Parameter(names = "-c")
      public boolean isC;
      @Parameter(names = {"-d", "--long-d"})
      public Integer d;
    }
    class MainParams {
      @Parameter(names = "-a")
      public boolean isA;
      @Parameter(names = {"-b", "--long-b"})
      public String bValue = "";
      @ParametersDelegate
      public ComplexDelegate delegate = new ComplexDelegate();
    }

    MainParams p = new MainParams();
    JCommander cmd = new JCommander(p);
    cmd.parse("-c", "--long-d", "123", "--long-b", "bValue");
    Assert.assertFalse(p.isA);
    Assert.assertEquals(p.bValue, "bValue");
    Assert.assertTrue(p.delegate.isC);
    Assert.assertEquals(p.delegate.d, Integer.valueOf(123));
  }

  @Test
  public void combinedAndNestedDelegates() {
    abstract class LeafAbstractDelegate {
      abstract float getFloat();
    }
    class LeafDelegate {
      @Parameter(names = "--list")
      public List<String> list = new ArrayList<String>() {{
        add("value1");
        add("value2");
      }};
      @Parameter(names = "--bool")
      public boolean bool;
    }
    class NestedDelegate1 {
      @ParametersDelegate
      public LeafDelegate leafDelegate = new LeafDelegate();
      @Parameter(names = {"-d", "--long-d"})
      public Integer d;
    }
    class NestedDelegate2 {
      @Parameter(names = "-c")
      public boolean isC;
      @ParametersDelegate
      public NestedDelegate1 nestedDelegate1 = new NestedDelegate1();
      @ParametersDelegate
      public LeafAbstractDelegate anonymousDelegate = new LeafAbstractDelegate() {
        @Parameter(names = "--anon-float")
        public float anon = 999f;

        @Override
        float getFloat() {
          return anon;
        }
      };
    }
    class MainParams {
      @Parameter(names = "-a")
      public boolean isA;
      @Parameter(names = {"-b", "--long-b"})
      public String bValue = "";
      @ParametersDelegate
      public NestedDelegate2 nestedDelegate2 = new NestedDelegate2();
    }

    MainParams p = new MainParams();
    JCommander cmd = new JCommander(p);
    cmd.parse("--anon-float 1.2 -d 234 --list a --list b -a".split(" "));
    Assert.assertEquals(p.nestedDelegate2.anonymousDelegate.getFloat(), 1.2f);
    Assert.assertEquals(p.nestedDelegate2.nestedDelegate1.leafDelegate.list, new ArrayList<String>() {{
      add("a");
      add("b");
    }});
    Assert.assertFalse(p.nestedDelegate2.nestedDelegate1.leafDelegate.bool);
    Assert.assertEquals(p.nestedDelegate2.nestedDelegate1.d, Integer.valueOf(234));
    Assert.assertFalse(p.nestedDelegate2.isC);
    Assert.assertTrue(p.isA);
    Assert.assertEquals(p.bValue, "");
  }

  @Test
  public void commandTest() {
    class Delegate {
      @Parameter(names = "-a")
      public String a = "b";
    }
    class Command {
      @ParametersDelegate
      public Delegate delegate = new Delegate();
    }

    Command c = new Command();

    JCommander cmd = new JCommander();
    cmd.addCommand("command", c);

    cmd.parse("command -a a".split(" "));
    Assert.assertEquals(c.delegate.a, "a");
  }

  @Test
  public void mainParametersTest() {
    class Delegate {
      @Parameter
      public List<String> mainParams = new ArrayList<>();
    }
    class Command {
      @ParametersDelegate
      public Delegate delegate = new Delegate();
    }

    Command c = new Command();

    JCommander cmd = new JCommander();
    cmd.addCommand("command", c);

    cmd.parse("command main params".split(" "));
    Assert.assertEquals(c.delegate.mainParams, new ArrayList<String>() {{
      add("main");
      add("params");
    }});
  }

  @Test(expectedExceptions = ParameterException.class,
          expectedExceptionsMessageRegExp = ".*delegate.*null.*")
  public void nullDelegatesAreProhibited() {
    class ComplexDelegate {
    }
    class MainParams {
      @ParametersDelegate
      public ComplexDelegate delegate;
    }

    MainParams p = new MainParams();
    JCommander cmd = new JCommander(p);
    cmd.parse();
  }

  @Test(expectedExceptions = ParameterException.class,
          expectedExceptionsMessageRegExp = ".*-a.*")
  public void duplicateDelegateThrowDuplicateOptionException() {
    class Delegate {
      @Parameter(names = "-a")
      public String a;
    }
    class MainParams {
      @ParametersDelegate
      public Delegate d1 = new Delegate();
      @ParametersDelegate
      public Delegate d2 = new Delegate();
    }

    MainParams p = new MainParams();
    JCommander cmd = new JCommander(p);
    cmd.parse("-a value".split(" "));
  }

  @Test(expectedExceptions = ParameterException.class, expectedExceptionsMessageRegExp = "Only one.*is allowed.*")
  public void duplicateMainParametersAreNotAllowed() {
    class Delegate1 {
      @Parameter
      public List<String> mainParams1 = new ArrayList<>();
    }
    class Delegate2 {
      @Parameter
      public List<String> mainParams2 = new ArrayList<>();
    }
    class Command {
      @ParametersDelegate
      public Delegate1 delegate1 = new Delegate1();
      @ParametersDelegate
      public Delegate2 delegate2 = new Delegate2();
    }

    Command c = new Command();

    JCommander cmd = new JCommander();
    cmd.addCommand("command", c);

    cmd.parse("command main params".split(" "));
  }

  public static void main(String[] args) {
    new ParametersDelegateTest().commandTest();
  }
}
