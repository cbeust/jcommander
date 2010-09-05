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
import com.beust.jcommander.args.ArgsConverter;
import com.beust.jcommander.args.ArgsI18N1;
import com.beust.jcommander.args.ArgsI18N2;
import com.beust.jcommander.args.ArgsI18N2New;
import com.beust.jcommander.args.ArgsInherited;
import com.beust.jcommander.args.ArgsMaster;
import com.beust.jcommander.args.ArgsMultipleUnparsed;
import com.beust.jcommander.args.ArgsPassword;
import com.beust.jcommander.args.ArgsPrivate;
import com.beust.jcommander.args.ArgsRequired;
import com.beust.jcommander.args.ArgsSlave;
import com.beust.jcommander.args.ArgsSlaveBogus;
import com.beust.jcommander.args.SeparatorColon;
import com.beust.jcommander.args.SeparatorEqual;
import com.beust.jcommander.args.SeparatorMixed;
import com.beust.jcommander.args.SlashSeparator;
import com.beust.jcommander.command.CommandAdd;
import com.beust.jcommander.command.CommandCommit;
import com.beust.jcommander.command.CommandMain;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
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
  public void separatorEqual() {
    SeparatorEqual s = new SeparatorEqual();
    String[] argv = { "-log=3", "--longoption=10" };
    new JCommander(s, argv);
    Assert.assertEquals(s.log.intValue(), 3);
    Assert.assertEquals(s.longOption.intValue(), 10);
  }

  @Test
  public void separatorColon() {
    SeparatorColon s = new SeparatorColon();
    String[] argv = { "-verbose:true" };
    new JCommander(s, argv);
    Assert.assertTrue(s.verbose);
  }

  @Test
  public void separatorBoth() {
    SeparatorColon s = new SeparatorColon();
    SeparatorEqual s2 = new SeparatorEqual();
    String[] argv = { "-verbose:true", "-log=3" };
    new JCommander(new Object[] { s, s2 }, argv);
    Assert.assertTrue(s.verbose);
    Assert.assertEquals(s2.log.intValue(), 3);
  }

  @Test
  public void separatorMixed1() {
    SeparatorMixed s = new SeparatorMixed();
    String[] argv = { "-long:1", "-level=42" };
    new JCommander(s, argv);
    Assert.assertEquals(s.l.longValue(), 1l);
    Assert.assertEquals(s.level.intValue(), 42);
  }

  @Test
  public void slashParameters() {
    SlashSeparator a = new SlashSeparator();
    String[] argv = { "/verbose", "/file", "/tmp/a" };
    new JCommander(a, argv);
    Assert.assertTrue(a.verbose);
    Assert.assertEquals(a.file, "/tmp/a");
  }

  @Test
  public void inheritance() {
    ArgsInherited args = new ArgsInherited();
    String[] argv = { "-log", "3", "-child", "2" };
    new JCommander(args, argv);
    Assert.assertEquals(args.child.intValue(), 2);
    Assert.assertEquals(args.log.intValue(), 3);
  }

  @Test
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

  @Test
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

  @Test
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

  public static void main(String[] args) {
    ArgsPassword a = new ArgsPassword();
    JCommander jc = new JCommander(a);
    jc.parse("-password");
    System.out.println("Password:" + a.password);
//    new JCommanderTest().commandsShouldBeShownInOrderOfInsertion();
//    CommandMain cm = new CommandMain();
//    JCommander jc = new JCommander(cm);
//    CommandAdd add = new CommandAdd();
//    jc.addCommand("add", add);
//    CommandCommit commit = new CommandCommit();
//    jc.addCommand("commit", commit);
//    jc.usage();

//    new JCommanderTest().requiredMainParameters();
//    new CommandTest().commandTest1();
//    new DefaultProviderTest().defaultProvider1();
//    ArgsMainParameter a = new ArgsMainParameter();
//    new JCommander(a, "ex1:10", "ex2:20");
//    System.out.println(a.parameters.get(0).host);
//    new JCommander(new Args1()).usage();
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
