/**
 * Copyright (C) 2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beust.jcommander;

import com.beust.jcommander.args.Args1;
import com.beust.jcommander.args.Args2;
import com.beust.jcommander.args.ArgsArityString;
import com.beust.jcommander.args.ArgsBooleanArity;
import com.beust.jcommander.args.ArgsBooleanArity0;
import com.beust.jcommander.args.ArgsConverter;
import com.beust.jcommander.args.ArgsEnum;
import com.beust.jcommander.args.ArgsEquals;
import com.beust.jcommander.args.ArgsHelp;
import com.beust.jcommander.args.ArgsI18N1;
import com.beust.jcommander.args.ArgsI18N2;
import com.beust.jcommander.args.ArgsI18N2New;
import com.beust.jcommander.args.ArgsInherited;
import com.beust.jcommander.args.ArgsList;
import com.beust.jcommander.args.ArgsMainParameter1;
import com.beust.jcommander.args.ArgsMaster;
import com.beust.jcommander.args.ArgsMultipleUnparsed;
import com.beust.jcommander.args.ArgsOutOfMemory;
import com.beust.jcommander.args.ArgsPrivate;
import com.beust.jcommander.args.ArgsRequired;
import com.beust.jcommander.args.ArgsSlave;
import com.beust.jcommander.args.ArgsSlaveBogus;
import com.beust.jcommander.args.ArgsValidate1;
import com.beust.jcommander.args.ArgsWithSet;
import com.beust.jcommander.args.Arity1;
import com.beust.jcommander.args.SeparatorColon;
import com.beust.jcommander.args.SeparatorEqual;
import com.beust.jcommander.args.SeparatorMixed;
import com.beust.jcommander.args.SlashSeparator;
import com.beust.jcommander.args.VariableArity;
import com.beust.jcommander.command.CommandAdd;
import com.beust.jcommander.command.CommandCommit;
import com.beust.jcommander.command.CommandMain;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;

@Test
public class JCommanderTest {
  public void simpleArgs() throws ParseException {
    Args1 args = new Args1();
    String[] argv = { "-debug", "-log", "2", "-float", "1.2", "-double", "1.3", "-bigdecimal", "1.4",
            "-date", "2011-10-26", "-groups", "unit", "a", "b", "c" };
    new JCommander(args, argv);

    Assert.assertTrue(args.debug);
    Assert.assertEquals(args.verbose.intValue(), 2);
    Assert.assertEquals(args.groups, "unit");
    Assert.assertEquals(args.parameters, Arrays.asList("a", "b", "c"));
    Assert.assertEquals(args.floa, 1.2f, 0.1f);
    Assert.assertEquals(args.doub, 1.3f, 0.1f);
    Assert.assertEquals(args.bigd, new BigDecimal("1.4"));
    Assert.assertEquals(args.date, new SimpleDateFormat("yyyy-MM-dd").parse("2011-10-26"));
  }

  /**
   * Make sure that if there are args with multiple names (e.g. "-log" and "-verbose"),
   * the usage will only display it once.
   */
  public void repeatedArgs() {
    Args1 args = new Args1();
    String[] argv = { "-log", "2" };
    JCommander jc = new JCommander(args, argv);
    Assert.assertEquals(jc.getParameters().size(), 8);
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
   * Getting the description of a nonexistent command should throw an exception.
   */
  @Test(expectedExceptions = ParameterException.class)
  public void nonexistentCommandShouldThrow() {
    String[] argv = { };
    JCommander jc = new JCommander(new Object(), argv);
    jc.getCommandDescription("foo");
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

  public void multipleNames1() {
    multipleNames("-log");
  }

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

  public void i18nNoLocale() {
    i18n1("MessageBundle", null, "Host");
  }

  public void i18nUsLocale() {
    i18n1("MessageBundle", new Locale("en", "US"), "Host");
  }

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

  public void i18nWithResourceAnnotation() {
    i18n2(new ArgsI18N2());
  }

  public void i18nWithResourceAnnotationNew() {
    i18n2(new ArgsI18N2New());
  }

  public void noParseConstructor() {
    JCommander jCommander = new JCommander(new ArgsMainParameter1());
    jCommander.usage(new StringBuilder());
    // Before fix, this parse would throw an exception, because it calls createDescription, which
    // was already called by usage(), and can only be called once.
    jCommander.parse();
  }

  /**
   * Test a use case where there are required parameters, but you still want
   * to interrogate the options which are specified.
   */
  public void usageWithRequiredArgsAndResourceBundle() {
    ArgsHelp argsHelp = new ArgsHelp();
    JCommander jc = new JCommander(new Object[]{argsHelp, new ArgsRequired()},
        java.util.ResourceBundle.getBundle("MessageBundle"));
    // Should be able to display usage without triggering validation
    jc.usage(new StringBuilder());
    try {
      jc.parse("-h");
      Assert.fail("Should have thrown a required parameter exception");
    } catch (ParameterException e) {
      Assert.assertTrue(e.getMessage().contains("are required"));
    }
    Assert.assertTrue(argsHelp.help);
  }

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
  public void arity2Fail() {
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

  public void privateArgs() {
    ArgsPrivate args = new ArgsPrivate();
    new JCommander(args, "-verbose", "3");
    Assert.assertEquals(args.getVerbose().intValue(), 3);
  }

  public void converterArgs() {
    ArgsConverter args = new ArgsConverter();
    String fileName = "a";
    new JCommander(args, "-file", "/tmp/" + fileName, 
      "-listStrings", "Tuesday,Thursday",
      "-listInts", "-1,8",
      "-listBigDecimals", "-11.52,100.12");
    Assert.assertEquals(args.file.getName(), fileName);
    Assert.assertEquals(args.listStrings.size(), 2);
    Assert.assertEquals(args.listStrings.get(0), "Tuesday");
    Assert.assertEquals(args.listStrings.get(1), "Thursday");
    Assert.assertEquals(args.listInts.size(), 2);
    Assert.assertEquals(args.listInts.get(0).intValue(), -1);
    Assert.assertEquals(args.listInts.get(1).intValue(), 8);
    Assert.assertEquals(args.listBigDecimals.size(), 2);
    Assert.assertEquals(args.listBigDecimals.get(0), new BigDecimal("-11.52"));
    Assert.assertEquals(args.listBigDecimals.get(1), new BigDecimal("100.12"));
  }

  private void argsBoolean1(String[] params, Boolean expected) {
    ArgsBooleanArity args = new ArgsBooleanArity();
    new JCommander(args, params);
    Assert.assertEquals(args.debug, expected);
  }

  private void argsBoolean0(String[] params, Boolean expected) {
    ArgsBooleanArity0 args = new ArgsBooleanArity0();
    new JCommander(args, params);
    Assert.assertEquals(args.debug, expected);
  }

  public void booleanArity1() {
    argsBoolean1(new String[] {}, Boolean.FALSE);
    argsBoolean1(new String[] { "-debug", "true" }, Boolean.TRUE);
  }

  public void booleanArity0() {
    argsBoolean0(new String[] {}, Boolean.FALSE);
    argsBoolean0(new String[] { "-debug"}, Boolean.TRUE);
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

  public void listParameters() {
    Args2 a = new Args2();
    String[] argv = {"-log", "2", "-groups", "unit", "a", "b", "c", "-host", "host2"};
    new JCommander(a, argv);
    Assert.assertEquals(a.verbose.intValue(), 2);
    Assert.assertEquals(a.groups, "unit");
    Assert.assertEquals(a.hosts, Arrays.asList("host2"));
    Assert.assertEquals(a.parameters, Arrays.asList("a", "b", "c"));
  }

  public void separatorEqual() {
    SeparatorEqual s = new SeparatorEqual();
    String[] argv = { "-log=3", "--longoption=10" };
    new JCommander(s, argv);
    Assert.assertEquals(s.log.intValue(), 3);
    Assert.assertEquals(s.longOption.intValue(), 10);
  }

  public void separatorColon() {
    SeparatorColon s = new SeparatorColon();
    String[] argv = { "-verbose:true" };
    new JCommander(s, argv);
    Assert.assertTrue(s.verbose);
  }

  public void separatorBoth() {
    SeparatorColon s = new SeparatorColon();
    SeparatorEqual s2 = new SeparatorEqual();
    String[] argv = { "-verbose:true", "-log=3" };
    new JCommander(new Object[] { s, s2 }, argv);
    Assert.assertTrue(s.verbose);
    Assert.assertEquals(s2.log.intValue(), 3);
  }

  public void separatorMixed1() {
    SeparatorMixed s = new SeparatorMixed();
    String[] argv = { "-long:1", "-level=42" };
    new JCommander(s, argv);
    Assert.assertEquals(s.l.longValue(), 1l);
    Assert.assertEquals(s.level.intValue(), 42);
  }

  public void slashParameters() {
    SlashSeparator a = new SlashSeparator();
    String[] argv = { "/verbose", "/file", "/tmp/a" };
    new JCommander(a, argv);
    Assert.assertTrue(a.verbose);
    Assert.assertEquals(a.file, "/tmp/a");
  }

  public void inheritance() {
    ArgsInherited args = new ArgsInherited();
    String[] argv = { "-log", "3", "-child", "2" };
    new JCommander(args, argv);
    Assert.assertEquals(args.child.intValue(), 2);
    Assert.assertEquals(args.log.intValue(), 3);
  }

  public void negativeNumber() {
    Args1 a = new Args1();
    String[] argv = { "-verbose", "-3" };
    new JCommander(a, argv);
    Assert.assertEquals(a.verbose.intValue(), -3);
  }

  @Test(expectedExceptions = ParameterException.class)
  public void requiredMainParameters() {
    ArgsRequired a = new ArgsRequired();
    String[] argv = {};
    new JCommander(a, argv);
  }

  public void usageShouldNotChange() {
    JCommander jc = new JCommander(new Args1(), new String[]{"-log", "1"});
    StringBuilder sb = new StringBuilder();
    jc.usage(sb);
    String expected = sb.toString();
    jc = new JCommander(new Args1(), new String[]{"-debug", "-log", "2", "-long", "5"});
    sb = new StringBuilder();
    jc.usage(sb);
    String actual = sb.toString();
    Assert.assertEquals(actual, expected);
  }

  private void verifyCommandOrdering(String[] commandNames, Object[] commands) {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);

    for (int i = 0; i < commands.length; i++) {
      jc.addCommand(commandNames[i], commands[i]);
    }

    Map<String, JCommander> c = jc.getCommands();
    Assert.assertEquals(c.size(), commands.length);

    Iterator<String> it = c.keySet().iterator();
    for (int i = 0; i < commands.length; i++) {
      Assert.assertEquals(it.next(), commandNames[i]);
    }
  }

  public void commandsShouldBeShownInOrderOfInsertion() {
    verifyCommandOrdering(new String[] { "add", "commit" },
        new Object[] { new CommandAdd(), new CommandCommit() });
    verifyCommandOrdering(new String[] { "commit", "add" },
        new Object[] { new CommandCommit(), new CommandAdd() });
  }

  @DataProvider
  public static Object[][] f() {
    return new Integer[][] {
      new Integer[] { 3, 5, 1 },
      new Integer[] { 3, 8, 1 },
      new Integer[] { 3, 12, 2 },
      new Integer[] { 8, 12, 2 },
      new Integer[] { 9, 10, 1 },
    };
  }

  @Test(expectedExceptions = ParameterException.class)
  public void arity1Fail() {
    final Arity1 arguments = new Arity1();
    final JCommander jCommander = new JCommander(arguments);
    final String[] commands = {
        "-inspect"
    };
    jCommander.parse(commands);
  }

  public void arity1Success1() {
    final Arity1 arguments = new Arity1();
    final JCommander jCommander = new JCommander(arguments);
    final String[] commands = {
        "-inspect", "true"
    };
    jCommander.parse(commands);
    Assert.assertTrue(arguments.inspect);
  }

  public void arity1Success2() {
    final Arity1 arguments = new Arity1();
    final JCommander jCommander = new JCommander(arguments);
    final String[] commands = {
        "-inspect", "false"
    };
    jCommander.parse(commands);
    Assert.assertFalse(arguments.inspect);
  }

  @Parameters(commandDescription = "Help for the given commands.")
  public static class Help {
      public static final String NAME = "help";

      @Parameter(description = "List of commands.")
      public List<String> commands=new ArrayList<String>();
  }

  @Test(expectedExceptions = ParameterException.class,
      description = "Verify that the main parameter's type is checked to be a List")
  public void wrongMainTypeShouldThrow() {
    JCommander jc = new JCommander(new ArgsRequiredWrongMain());
    jc.parse(new String[] { "f1", "f2" });
  }

  @Test(description = "This used to run out of memory")
  public void oom() {
    JCommander jc = new JCommander(new ArgsOutOfMemory());
    jc.usage(new StringBuilder());
  }

  @Test
  public void getParametersShouldNotNpe() {
    JCommander jc = new JCommander(new Args1());
    List<ParameterDescription> parameters = jc.getParameters();
  }

  public void validationShouldWork1() {
    ArgsValidate1 a = new ArgsValidate1();
    JCommander jc = new JCommander(a);
    jc.parse(new String[] { "-age", "2 "});
    Assert.assertEquals(a.age, new Integer(2));
  }

  @Test(expectedExceptions = ParameterException.class)
  public void validationShouldWorkWithDefaultValues() {
    ArgsValidate2 a = new ArgsValidate2();
    new JCommander(a);
  }

  @Test(expectedExceptions = ParameterException.class)
  public void validationShouldWork2() {
    ArgsValidate1 a = new ArgsValidate1();
    JCommander jc = new JCommander(a);
    jc.parse(new String[] { "-age", "-2 "});
  }

  public void atFileCanContainEmptyLines() throws IOException {
    File f = File.createTempFile("JCommander", null);
    f.deleteOnExit();
    FileWriter fw = new FileWriter(f);
    fw.write("-log\n");
    fw.write("\n");
    fw.write("2\n");
    fw.close();
    new JCommander(new Args1(), "@" + f.getAbsolutePath());
  }

  public void handleEqualSigns() {
    ArgsEquals a = new ArgsEquals();
    JCommander jc = new JCommander(a);
    jc.parse(new String[] { "-args=a=b,b=c" });
    Assert.assertEquals(a.args, "a=b,b=c");
  }

  @SuppressWarnings("serial")
  public void handleSets() {
    ArgsWithSet a = new ArgsWithSet();
    new JCommander(a, new String[] { "-s", "3,1,2" });
    Assert.assertEquals(a.set, new TreeSet<Integer>() {{ add(1); add(2); add(3); }});
  }

  private static final List<String> V = Arrays.asList("a", "b", "c", "d");

  @DataProvider
  public Object[][] variable() {
    return new Object[][] {
        new Object[] { 0, V.subList(0, 0), V },
        new Object[] { 1, V.subList(0, 1), V.subList(1, 4) },
        new Object[] { 2, V.subList(0, 2), V.subList(2, 4) },
        new Object[] { 3, V.subList(0, 3), V.subList(3, 4) },
        new Object[] { 4, V.subList(0, 4), V.subList(4, 4) },
    };
  }

  @Test(dataProvider = "variable")
  public void variableArity(int count, List<String> var, List<String> main) {
    VariableArity va = new VariableArity(count);
    new JCommander(va).parse("-variable", "a", "b", "c", "d");
    Assert.assertEquals(var, va.var);
    Assert.assertEquals(main, va.main);
  }

  public void enumArgs() {
    ArgsEnum args = new ArgsEnum();
    String[] argv = { "-choice", "ONE"};
    new JCommander(args, argv);

    Assert.assertEquals(args.choice, ArgsEnum.ChoiceType.ONE);
  }

  @Test(expectedExceptions = ParameterException.class)
  public void enumArgsFail() {
    ArgsEnum args = new ArgsEnum();
    String[] argv = { "-choice", "A" };
    new JCommander(args, argv);
  }

  public void testListAndSplitters() {
    ArgsList al = new ArgsList();
    JCommander j = new JCommander(al);
    j.parse("-groups", "a,b", "-ints", "41,42", "-hp", "localhost:1000;example.com:1001",
        "-hp2", "localhost:1000,example.com:1001", "-uppercase", "ab,cd");
    Assert.assertEquals(al.groups.get(0), "a");
    Assert.assertEquals(al.groups.get(1), "b");
    Assert.assertEquals(al.ints.get(0).intValue(), 41);
    Assert.assertEquals(al.ints.get(1).intValue(), 42);
    Assert.assertEquals(al.hostPorts.get(0).host, "localhost");
    Assert.assertEquals(al.hostPorts.get(0).port.intValue(), 1000);
    Assert.assertEquals(al.hostPorts.get(1).host, "example.com");
    Assert.assertEquals(al.hostPorts.get(1).port.intValue(), 1001);
    Assert.assertEquals(al.hp2.get(1).host, "example.com");
    Assert.assertEquals(al.hp2.get(1).port.intValue(), 1001);
    Assert.assertEquals(al.uppercase.get(0), "AB");
    Assert.assertEquals(al.uppercase.get(1), "CD");
  }

  @Test(expectedExceptions = ParameterException.class)
  public void shouldThrowIfUnknownOption() {
    class A {
      @Parameter(names = "-long")
      public long l;
    }
    A a = new A();
    new JCommander(a).parse("-lon", "32");
  }

  @Test(expectedExceptions = ParameterException.class)
  public void mainParameterShouldBeValidate() {
    class V implements IParameterValidator {

      public void validate(String name, String value) throws ParameterException {
        Assert.assertEquals("a", value);
      }
    }

    class A {
      @Parameter(validateWith = V.class)
      public List<String> m;
    }

    A a = new A();
    new JCommander(a).parse("b");
  }

  @Test(enabled = false)
  public static void main(String[] args) throws Exception {

    System.out.println("A");
    class A {
      @Parameter
      List<String> parameters;

      @Parameter(names = "-long")
      public long l;
    }
    A a = new A();
    new JCommander(a).parse("-long", "32");
//    System.out.println(a.l);
//    System.out.println(a.parameters);
//    ArgsList al = new ArgsList();
//    JCommander j = new JCommander(al);
//    j.setColumnSize(40);
//    j.usage();
//    new JCommanderTest().testListAndSplitters();
//    new JCommanderTest().converterArgs();
  }

  // Tests:
  // required unparsed parameter
}
