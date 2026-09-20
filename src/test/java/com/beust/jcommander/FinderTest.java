package com.beust.jcommander;

import com.beust.jcommander.JCommanderTest.BaseArgs;
import com.beust.jcommander.JCommanderTest.ConfigureArgs;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Test
public class FinderTest {
  public void caseInsensitiveOption() {
    class Arg {
  
      @Parameter(names = { "-p", "--param" })
      private String param;
    }
    Arg a = new Arg();
    JCommander jc = new JCommander(a);
    jc.setCaseSensitiveOptions(false);
    jc.parse("--PARAM", "foo");
    Assert.assertEquals(a.param, "foo");
  }

  public void caseInsensitiveCommand() {
    BaseArgs a = new BaseArgs();
    ConfigureArgs conf = new ConfigureArgs();
    JCommander jc = new JCommander(a);
    jc.addCommand(conf);
    jc.setCaseSensitiveOptions(false);
    jc.parse("--CONFIGURE");
    String command = jc.getParsedCommand();
    Assert.assertEquals(command, "--configure");
  }


  public void caseInsensitiveArguments() throws Exception {
    @Parameters(separators = "=")
    class TestJobRunnerArgs {
      @Parameter(names = "someParameter2")
      private String someParameter2;
    }

    String[] testValues = {"someparameter2=2"};
    TestJobRunnerArgs testJobRunnerArgs = new TestJobRunnerArgs();

    JCommander jCommander = JCommander.newBuilder().addObject(testJobRunnerArgs).build();
    jCommander.setCaseSensitiveOptions(false);
    jCommander.parse(testValues);
    Assert.assertEquals("2", testJobRunnerArgs.someParameter2);
  }

  public void abbreviatedOptions() {
    class Arg {
      @Parameter(names = { "-p", "--param" })
      private String param;
    }
    Arg a = new Arg();
    JCommander jc = new JCommander(a);
    jc.setAllowAbbreviatedOptions(true);
    jc.parse("--par", "foo");
    Assert.assertEquals(a.param, "foo");
  }

  public void abbreviatedOptionsCaseInsensitive() {
    class Arg {
      @Parameter(names = { "-p", "--param" })
      private String param;
    }
    Arg a = new Arg();
    JCommander jc = new JCommander(a);
    jc.setCaseSensitiveOptions(false);
    jc.setAllowAbbreviatedOptions(true);
    jc.parse("--PAR", "foo");
    Assert.assertEquals(a.param, "foo");
  }

  public void caseInsensitiveOptionWithTurkishDefaultLocale() {
    class Arg {
      @Parameter(names = "--input")
      private String input;
    }

    Arg a = new Arg();
    JCommander jc = new JCommander(a);
    jc.setCaseSensitiveOptions(false);

    LocaleTestHelper.withDefaultLocale(Locale.forLanguageTag("tr-TR"), () -> jc.parse("--INPUT", "value"));

    Assert.assertEquals(a.input, "value");
  }

  public void abbreviatedOptionsCaseInsensitiveWithTurkishDefaultLocale() {
    class Arg {
      @Parameter(names = "--input")
      private String input;
    }

    Arg a = new Arg();
    JCommander jc = new JCommander(a);
    jc.setCaseSensitiveOptions(false);
    jc.setAllowAbbreviatedOptions(true);

    LocaleTestHelper.withDefaultLocale(Locale.forLanguageTag("tr-TR"), () -> jc.parse("--INP", "value"));

    Assert.assertEquals(a.input, "value");
  }

  public void caseInsensitiveOptionStopsVariableArityWithTurkishDefaultLocale() {
    class Arg {
      @Parameter(names = "--files", variableArity = true)
      private List<String> files = new ArrayList<>();

      @Parameter(names = "--input")
      private String input;
    }

    Arg a = new Arg();
    JCommander jc = new JCommander(a);
    jc.setCaseSensitiveOptions(false);

    LocaleTestHelper.withDefaultLocale(Locale.forLanguageTag("tr-TR"),
        () -> jc.parse("--files", "first.txt", "--INPUT", "value.txt"));

    Assert.assertEquals(a.files, List.of("first.txt"));
    Assert.assertEquals(a.input, "value.txt");
  }

  @Test(expectedExceptions = ParameterException.class)
  public void ambiguousAbbreviatedOptions() {
    class Arg {
      @Parameter(names = { "--param" })
      private String param;
      @Parameter(names = { "--parb" })
      private String parb;
    }
    Arg a = new Arg();
    JCommander jc = new JCommander(a);
    jc.setAllowAbbreviatedOptions(true);
    jc.parse("--par", "foo");
    Assert.assertEquals(a.param, "foo");
  }

  @Test(expectedExceptions = ParameterException.class)
  public void ambiguousAbbreviatedOptionsCaseInsensitive() {
    class Arg {
      @Parameter(names = { "--param" })
      private String param;
      @Parameter(names = { "--parb" })
      private String parb;
    }
    Arg a = new Arg();
    JCommander jc = new JCommander(a);
    jc.setCaseSensitiveOptions(false);
    jc.setAllowAbbreviatedOptions(true);
    jc.parse("--PAR", "foo");
    Assert.assertEquals(a.param, "foo");
  }

  @Test(enabled = false)
  public static void main(String[] args) throws Exception {
    new FinderTest().ambiguousAbbreviatedOptionsCaseInsensitive();
  }

}
