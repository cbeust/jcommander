package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class JCommanderTest {
  @Parameter
  public List<String> parameters = Lists.newArrayList();

  @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity", required = true)
  public Integer verbose = 1;

  @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
  public String groups;

  @Parameter(names = "-debug", description = "Debug mode")
  public boolean debug = false;

  @Test
  public void simpleArgs() {
    JCommanderTest jct = new JCommanderTest();
    String[] argv = { "-log", "2", "-groups", "unit", "a", "b", "c" };
    new JCommander(jct, argv);

    System.out.println("Verbose:" + verbose);
    Assert.assertEquals(jct.verbose.intValue(), 2);
    Assert.assertEquals(jct.groups, "unit");
    Assert.assertEquals(jct.parameters, Arrays.asList("a", "b", "c"));
  }

  /**
   * Make sure that if there are args with multiple names (e.g. "-log" and "-verbose"),
   * the usage will only display it once.
   */
  @Test
  public void repeatedArgs() {
    JCommanderTest jct = new JCommanderTest();
    String[] argv = { "-log", "2" };
    JCommander jc = new JCommander(jct, argv);
    Assert.assertEquals(jc.getParameters().size(), 3);
  }

  /**
   * Not specifying a required option should throw an exception.
   */
  @Test(expectedExceptions = ParameterException.class)
  public void requiredFields1() {
    JCommanderTest jct = new JCommanderTest();
    String[] argv = { "-debug" };
    JCommander jc = new JCommander(jct, argv);
  }

  /**
   * Required options with multiple names should work with all names.
   */
  @Test
  public void requiredFields2() {
    JCommanderTest jct = new JCommanderTest();
    String[] argv = { "-log", "2" };
    JCommander jc = new JCommander(jct, argv);
  }

  /**
   * Required options with multiple names should work with all names.
   */
  @Test
  public void requiredFields3() {
    JCommanderTest jct = new JCommanderTest();
    String[] argv = { "-verbose", "2" };
    JCommander jc = new JCommander(jct, argv);
  }

  private void i18n(Locale locale, String expectedString) {
    ResourceBundle bundle = locale != null ? ResourceBundle.getBundle("MessageBundle", locale)
        : null;

    I18N i18n = new I18N();
    String[] argv = { "-host", "localhost" };
    JCommander jc = new JCommander(i18n, bundle, argv);
//    jc.usage();

    ParameterDescription pd = jc.getParameters().get(0);
    Assert.assertEquals(pd.getDescription(), expectedString);
  }

  @Test
  public void i18nNoLocale() {
    i18n(null, "Host");
  }

  @Test
  public void i18nUsLocale() {
    i18n(new Locale("en", "US"), "Host");
  }

  @Test
  public void i18nFrLocale() {
    i18n(new Locale("fr", "FR"), "H™te");
  }

  public static void main(String[] args) {
    new JCommanderTest().i18nFrLocale();
  }
}
