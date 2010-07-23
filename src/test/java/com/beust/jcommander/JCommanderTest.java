package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class JCommanderTest {
  @Test
  public void simpleArgs() {
    Args1 args = new Args1();
    String[] argv = { "-debug", "-log", "2", "-groups", "unit", "a", "b", "c" };
    new JCommander(args, argv);

    Assert.assertTrue(args.debug);
    Assert.assertEquals(args.verbose.intValue(), 2);
    Assert.assertEquals(args.groups, "unit");
    Assert.assertEquals(args.parameters, Arrays.asList("a", "b", "c"));
  }

  /**
   * Make sure that if there are args with multiple names (e.g. "-log" and "-verbose"),
   * the usage will only display it once.
   */
  @Test
  public void repeatedArgs() {
    Args1 args = new Args1();
    String[] argv = { "-log", "2" };
    JCommander jc = new JCommander(args, argv);
    Assert.assertEquals(jc.getParameters().size(), 4);
  }

  /**
   * Not specifying a required option should throw an exception.
   */
  @Test(expectedExceptions = ParameterException.class)
  public void requiredFields1Fail() {
    Args1 args = new Args1();
    String[] argv = { "-debug" };
    new JCommander(args, argv);
  }

  /**
   * Required options with multiple names should work with all names.
   */
  private void multipleNames(String option) {
    Args1 args = new Args1();
    String[] argv = { option, "2" };
    new JCommander(args, argv);
    Assert.assertEquals(args.verbose.intValue(), 2);
  }
  
  @Test
  public void multipleNames1() {
    multipleNames("-log");
  }

  @Test
  public void multipleNames2() {
    multipleNames("-verbose");
  }

  private void i18n1(String bundleName, Locale locale, String expectedString) {
    ResourceBundle bundle = locale != null ? ResourceBundle.getBundle(bundleName, locale)
        : null;

    ArgsI18N1 i18n = new ArgsI18N1();
    String[] argv = { "-host", "localhost" };
    JCommander jc = new JCommander(i18n, bundle, argv);
//    jc.usage();

    ParameterDescription pd = jc.getParameters().get(0);
    Assert.assertEquals(pd.getDescription(), expectedString);
  }

  @Test
  public void i18nNoLocale() {
    i18n1("MessageBundle", null, "Host");
  }

  @Test
  public void i18nUsLocale() {
    i18n1("MessageBundle", new Locale("en", "US"), "Host");
  }

  @Test
  public void i18nFrLocale() {
    i18n1("MessageBundle", new Locale("fr", "FR"), "Hôte");
  }

  private void i18n2(Object i18n) {
    String[] argv = { "-host", "localhost" };
    Locale.setDefault(new Locale("fr", "FR"));
    JCommander jc = new JCommander(i18n, argv);
    ParameterDescription pd = jc.getParameters().get(0);
    Assert.assertEquals(pd.getDescription(), "Hôte");
  }

  @Test
  public void i18nWithResourceAnnotation() {
    i18n2(new ArgsI18N2());
  }

  @Test
  public void i18nWithResourceAnnotationNew() {
    i18n2(new ArgsI18N2New());
  }

  @Test
  public void multiObjects() {
    ArgsMaster m = new ArgsMaster();
    ArgsSlave s = new ArgsSlave();
    String[] argv = { "-master", "master", "-slave", "slave" };
    new JCommander(new Object[] { m , s }, argv);

    Assert.assertEquals(m.master, "master");
    Assert.assertEquals(s.slave, "slave");
  }

  @Test(expectedExceptions = ParameterException.class)
  public void multiObjectsWithDuplicatesFail() {
    ArgsMaster m = new ArgsMaster();
    ArgsSlave s = new ArgsSlaveBogus();
    String[] argv = { "-master", "master", "-slave", "slave" };
    new JCommander(new Object[] { m , s }, argv);
  }

  @Test
  public void arityString() {
    ArgsArityString args = new ArgsArityString();
    String[] argv = { "-pairs", "pair0", "pair1", "rest" };
    new JCommander(args, argv);

    Assert.assertEquals(args.pairs.size(), 2);
    Assert.assertEquals(args.pairs.get(0), "pair0");
    Assert.assertEquals(args.pairs.get(1), "pair1");
    Assert.assertEquals(args.rest.size(), 1);
    Assert.assertEquals(args.rest.get(0), "rest");
  }

  @Test(expectedExceptions = ParameterException.class)
  public void arity1Fail() {
    ArgsArityString args = new ArgsArityString();
    String[] argv = { "-pairs", "pair0" };
    new JCommander(args, argv);
  }

  @Test(expectedExceptions = ParameterException.class)
  public void multipleUnparsedFail() {
    ArgsMultipleUnparsed args = new ArgsMultipleUnparsed();
    String[] argv = { };
    new JCommander(args, argv);
  }

  @Test
  public void privateArgs() {
    ArgsPrivate args = new ArgsPrivate();
    new JCommander(args, "-verbose", "3");
    Assert.assertEquals(args.getVerbose().intValue(), 3);
  }

  @Test
  public void converterArgs() {
    ArgsConverter args = new ArgsConverter();
    String fileName = "a";
    new JCommander(args, "-file", "/tmp/" + fileName, "-days", "Tuesday,Thursday");
    Assert.assertEquals(args.file.getName(), fileName);
    Assert.assertEquals(args.days.size(), 2);
    Assert.assertEquals(args.days.get(0), "Tuesday");
    Assert.assertEquals(args.days.get(1), "Thursday");
  }

  public void booleanArity() {
    ArgsBooleanArity args = new ArgsBooleanArity();
    new JCommander(args, "-debug", "true");
    Assert.assertEquals(args.debug, Boolean.TRUE);
  }

  @Test(expectedExceptions = ParameterException.class)
  public void badParameterShouldThrowParameter1Exception() {
    Args1 args = new Args1();
    String[] argv = { "-log", "foo" };
    new JCommander(args, argv);
  }


  @Test(expectedExceptions = ParameterException.class)
  public void badParameterShouldThrowParameter2Exception() {
    Args1 args = new Args1();
    String[] argv = { "-long", "foo" };
    new JCommander(args, argv);
  }

  @Test
  public void listParameters() {
    Args2 a = new Args2();
    String[] argv = {"-log", "2", "-groups", "unit", "a", "b", "c", "-host", "host2"};
    new JCommander(a, argv);
    Assert.assertEquals(a.verbose.intValue(), 2);
    Assert.assertEquals(a.groups, "unit");
    Assert.assertEquals(a.hosts, Arrays.asList("host2"));
    Assert.assertEquals(a.parameters, Arrays.asList("a", "b", "c"));
  }

  @Test
  public void separatorTest() {
    Separator s = new Separator();
    String[] argv = { "-log=3" };
    new JCommander(s, argv);
    Assert.assertEquals(s.log.intValue(), 3);
  }

  public static void main(String[] args) {
    new JCommanderTest().separatorTest();
//    Separator a = new Separator();
//    String[] argv = new String[] { "-n", "foo" };
//    String[] argv = new String[] { "-v", "t" };
//    String[] argv = { "-log=10" };
//    JCommander jc = new JCommander(a, argv);
//    Assert.assertEquals(a.log.intValue(), 10);
  }

  // Tests:
  // required unparsed parameter
}
