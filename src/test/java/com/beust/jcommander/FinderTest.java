package com.beust.jcommander;

import com.beust.jcommander.JCommanderTest.BaseArgs;
import com.beust.jcommander.JCommanderTest.ConfigureArgs;

import org.testng.Assert;
import org.testng.annotations.Test;

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
    jc.parse(new String[] { "--PARAM", "foo" });
    Assert.assertEquals(a.param, "foo");
  }

  public void caseInsensitiveCommand() {
    BaseArgs a = new BaseArgs();
    ConfigureArgs conf = new ConfigureArgs();
    JCommander jc = new JCommander(a);
    jc.addCommand(conf);
    jc.setCaseSensitiveOptions(false);
//    jc.setCaseSensitiveCommands(false);
    jc.parse("--CONFIGURE");
    String command = jc.getParsedCommand();
    Assert.assertEquals(command, "--configure");
  }

  public void abbreviatedOptions() {
    class Arg {
      @Parameter(names = { "-p", "--param" })
      private String param;
    }
    Arg a = new Arg();
    JCommander jc = new JCommander(a);
    jc.setAllowAbbreviatedOptions(true);
    jc.parse(new String[] { "--par", "foo" });
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
    jc.parse(new String[] { "--PAR", "foo" });
    Assert.assertEquals(a.param, "foo");
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
    jc.parse(new String[] { "--par", "foo" });
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
    jc.parse(new String[] { "--PAR", "foo" });
    Assert.assertEquals(a.param, "foo");
  }

  @Test(enabled = false)
  public static void main(String[] args) throws Exception {
    new FinderTest().ambiguousAbbreviatedOptionsCaseInsensitive();
  }

}
