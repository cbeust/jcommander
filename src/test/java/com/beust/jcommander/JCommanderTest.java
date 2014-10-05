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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.beust.jcommander.args.AlternateNamesForListArgs;
import com.beust.jcommander.args.Args1;
import com.beust.jcommander.args.Args1Setter;
import com.beust.jcommander.args.Args2;
import com.beust.jcommander.args.ArgsArityString;
import com.beust.jcommander.args.ArgsBooleanArity;
import com.beust.jcommander.args.ArgsBooleanArity0;
import com.beust.jcommander.args.ArgsConverter;
import com.beust.jcommander.args.ArgsEnum;
import com.beust.jcommander.args.ArgsEnum.ChoiceType;
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
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;

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

  @DataProvider
  public Object[][] alternateNamesListArgs() {
    return new Object[][] {
        new String[][] {new String[] {"--servers", "1", "-s", "2", "--servers", "3"}},
        new String[][] {new String[] {"-s", "1", "-s", "2", "--servers", "3"}},
        new String[][] {new String[] {"--servers", "1", "--servers", "2", "-s", "3"}},
        new String[][] {new String[] {"-s", "1", "--servers", "2", "-s", "3"}},
        new String[][] {new String[] {"-s", "1", "-s", "2", "--servers", "3"}},
    };
  }
  
  /**
   *  Confirm that List<?> parameters with alternate names return the correct
   * List regardless of how the arguments are specified
   */
  
  @Test(dataProvider = "alternateNamesListArgs")
  public void testAlternateNamesForListArguments(String[] argv) {
      AlternateNamesForListArgs args = new AlternateNamesForListArgs();
      
      new JCommander(args, argv);
      
      Assert.assertEquals(args.serverNames.size(), 3);
      Assert.assertEquals(args.serverNames.get(0), argv[1]);
      Assert.assertEquals(args.serverNames.get(1), argv[3]);
      Assert.assertEquals(args.serverNames.get(2), argv[5]);
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
    String[] argv = { "-choice", "ONE", "-choices", "ONE", "Two" };
    JCommander jc = new JCommander(args, argv);

    Assert.assertEquals(args.choice, ArgsEnum.ChoiceType.ONE);

    List<ChoiceType> expected = Arrays.asList(ChoiceType.ONE, ChoiceType.Two);
    Assert.assertEquals(expected, args.choices);
    Assert.assertEquals(jc.getParameters().get(0).getDescription(),
        "Options: " + EnumSet.allOf((Class<? extends Enum>) ArgsEnum.ChoiceType.class));

  }

  public void enumArgsCaseInsensitive() {
      ArgsEnum args = new ArgsEnum();
      String[] argv = { "-choice", "one"};
      JCommander jc = new JCommander(args, argv);

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

      @Override
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

  @Parameters(commandNames = { "--configure" })
  public static class ConfigureArgs {
  }

  public static class BaseArgs {
    @Parameter(names = { "-h", "--help" }, description = "Show this help screen")
    private boolean help = false;

    @Parameter(names = { "--version", "-version" }, description = "Show the program version")
    private boolean version;
  }

  public void commandsWithSamePrefixAsOptionsShouldWork() {
    BaseArgs a = new BaseArgs();
    ConfigureArgs conf = new ConfigureArgs();
    JCommander jc = new JCommander(a);
    jc.addCommand(conf);
    jc.parse("--configure");
  }

  // Tests:
  // required unparsed parameter
  @Test(enabled = false,
      description = "For some reason, this test still asks the password on stdin")
  public void askedRequiredPassword() {
    class A {     
        @Parameter(names = { "--password", "-p" }, description = "Private key password", 
            password = true, required = true)
        public String password;

        @Parameter(names = { "--port", "-o" }, description = "Port to bind server to",
            required = true)
        public int port;
    }
    A a = new A();
    InputStream stdin = System.in;
    try {
      System.setIn(new ByteArrayInputStream("password".getBytes()));      
      new JCommander(a,new String[]{"--port", "7","--password"});
      Assert.assertEquals(a.port, 7);
      Assert.assertEquals(a.password, "password");
    } finally {
      System.setIn(stdin);
    }
  }

  public void dynamicParameters() {
    class Command {
      @DynamicParameter(names = {"-P"}, description = "Additional command parameters")
      private Map<String, String> params = Maps.newHashMap();
    }
    JCommander commander = new JCommander();
    Command c = new Command();
    commander.addCommand("command", c);
    commander.parse(new String[] { "command", "-Pparam='name=value'" });
    Assert.assertEquals(c.params.get("param"), "'name=value'");
  }

  public void exeParser() {
      class Params {
        @Parameter( names= "-i")
        private String inputFile;
      }

      String args[] = { "-i", "" };
      Params p = new Params();
      new JCommander(p, args);
  }

  public void multiVariableArityList() {
    class Params {
      @Parameter(names = "-paramA", description = "ParamA", variableArity = true)
      private List<String> paramA = Lists.newArrayList();

      @Parameter(names = "-paramB", description = "ParamB", variableArity = true)
      private List<String> paramB = Lists.newArrayList();
    }

    {
      String args[] = { "-paramA", "a1", "a2", "-paramB", "b1", "b2", "b3" };
      Params p = new Params();
      new JCommander(p, args).parse();
      Assert.assertEquals(p.paramA, Arrays.asList(new String[] { "a1", "a2" }));
      Assert.assertEquals(p.paramB, Arrays.asList(new String[] { "b1", "b2", "b3" }));
    }

    {
      String args[] = { "-paramA", "a1", "a2", "-paramB", "b1", "-paramA", "a3" };
      Params p = new Params();
      new JCommander(p, args).parse();
      Assert.assertEquals(p.paramA, Arrays.asList(new String[] { "a1", "a2", "a3" }));
      Assert.assertEquals(p.paramB, Arrays.asList(new String[] { "b1" }));
    }
  }

  @Test(enabled = false,
      description = "Need to double check that the command description is i18n'ed in the usage")
  public void commandKey() {
    @Parameters(resourceBundle = "MessageBundle", commandDescriptionKey = "command")
    class Args {
      @Parameter(names="-myoption", descriptionKey="myoption")
      private boolean option; 
    }
    JCommander j = new JCommander();
    Args a = new Args();
    j.addCommand("comm", a);
    j.usage();
  }

  public void tmp() {
    class A {
      @Parameter(names = "-b")
      public String b;
    }
    new JCommander(new A()).parse("");
  }

  public void unknownOptionWithDifferentPrefix() {
    @Parameters(optionPrefixes = "/")
    class SlashSeparator {

     @Parameter(names = "/verbose")
     public boolean verbose = false;

     @Parameter(names = "/file")
     public String file;
    }
    SlashSeparator ss = new SlashSeparator();
    try {
      new JCommander(ss).parse("/notAParam");
    } catch (ParameterException ex) {
      boolean result = ex.getMessage().contains("Unknown option");
      Assert.assertTrue(result);
    }
  }

  public void equalSeparator() {
    @Parameters(separators = "=", commandDescription = "My command")
    class MyClass {

       @Parameter(names = { "-p", "--param" }, required = true, description = "param desc...")
       private String param;
    }
    MyClass c = new MyClass();
    String expected = "\"hello\"world";
    new JCommander(c).parse("--param=" + expected);
    Assert.assertEquals(expected, c.param);
  }

  public void simpleArgsSetter() throws ParseException {
    Args1Setter args = new Args1Setter();
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

  public void verifyHelp() {
    class Arg {
      @Parameter(names = "--help", help = true)
      public boolean help = false;

      @Parameter(names = "file", required = true)
      public String file;
    }
    Arg arg = new Arg();
    String[] argv = { "--help" };
    new JCommander(arg, argv);

    Assert.assertTrue(arg.help);
  }

  public void helpTest() {
    class Arg {
      @Parameter(names = { "?", "-help", "--help" }, description = "Shows help", help = true)
      private boolean help = false;
    }
    Arg arg = new Arg();
    JCommander jc = new JCommander(arg);
    jc.parse(new String[] { "-help" });
//    System.out.println("helpTest:" + arg.help);
  }

  @Test(enabled = false, description = "Should only be enable once multiple parameters are allowed")
  public void duplicateParameterNames() {
    class ArgBase {
      @Parameter(names = { "-host" })
      protected String host;
    }

    class Arg1 extends ArgBase {}
    Arg1 arg1 = new Arg1();

    class Arg2 extends ArgBase {}
    Arg2 arg2 = new Arg2();

    JCommander jc = new JCommander(new Object[] { arg1, arg2});
    jc.parse(new String[] { "-host", "foo" });
    Assert.assertEquals(arg1.host, "foo");
    Assert.assertEquals(arg2.host, "foo");
  }

  public void parameterWithOneDoubleQuote() {
    @Parameters(separators = "=")
    class Arg {
      @Parameter(names = { "-p", "--param" })
      private String param;
    }
    JCommander jc = new JCommander(new MyClass());
    jc.parse("-p=\"");
  }

  public void emptyStringAsDefault() {
    class Arg {
      @Parameter(names = "-x")
      String s = "";
    }
    Arg a = new Arg();
    StringBuilder sb = new StringBuilder();
    new JCommander(a).usage(sb);
    Assert.assertTrue(sb.toString().contains("Default: <empty string>"));
  }

  public void spaces() {
    class Arg {
      @Parameter(names = "-rule", description = "rule")
      private List<String> rules = new ArrayList<String>();
    }
    Arg a = new Arg();
    new JCommander(a, "-rule", "some test");
    Assert.assertEquals(a.rules, Arrays.asList("some test"));
  }

  static class V2 implements IParameterValidator2 {
    final static List<String> names =  Lists.newArrayList();
    static boolean validateCalled = false;

    @Override
    public void validate(String name, String value) throws ParameterException {
      validateCalled = true;
    }

    @Override
    public void validate(String name, String value, ParameterDescription pd)
        throws ParameterException {
      names.addAll(Arrays.asList(pd.getParameter().names()));
    }
  }

  public void validator2() {
    class Arg {
      @Parameter(names = { "-h", "--host" }, validateWith = V2.class)
      String host;
    }
    Arg a = new Arg();
    V2.names.clear();
    V2.validateCalled = false;
    JCommander jc = new JCommander(a, "--host", "h");
    jc.setAcceptUnknownOptions(true);
    Assert.assertEquals(V2.names, Arrays.asList(new String[] { "-h", "--host" }));
    Assert.assertTrue(V2.validateCalled);
  }
  
  public void usageCommandsUnderUsage() {
    class Arg {
    }
    @Parameters(commandDescription = "command a")
    class ArgCommandA {
      @Parameter(description = "command a parameters")
      List<String> parameters;
    }
    @Parameters(commandDescription = "command b")
    class ArgCommandB {
      @Parameter(description = "command b parameters")
      List<String> parameters;
    }
    
    Arg a = new Arg();
    
    JCommander c = new JCommander(a);
    c.addCommand("a", new ArgCommandA());
    c.addCommand("b", new ArgCommandB());
    
    StringBuilder sb = new StringBuilder();
    c.usage(sb);
    Assert.assertTrue(sb.toString().contains("[command options]\n  Commands:"));
  }

  public void usageWithEmpytLine() {
    class Arg {
    }
    @Parameters(commandDescription = "command a")
    class ArgCommandA {
      @Parameter(description = "command a parameters")
      List<String> parameters;
    }
    @Parameters(commandDescription = "command b")
    class ArgCommandB {
      @Parameter(description = "command b parameters")
      List<String> parameters;
    }
    
    Arg a = new Arg();
    
    JCommander c = new JCommander(a);
    c.addCommand("a", new ArgCommandA());
    c.addCommand("b", new ArgCommandB());
    
    StringBuilder sb = new StringBuilder();
    c.usage(sb);
    Assert.assertTrue(sb.toString().contains("command a parameters\n\n    b"));
  }

  public void partialValidation() {
    class Arg {
      @Parameter(names = { "-h", "--host" })
      String host;
    }
    Arg a = new Arg();
    JCommander jc = new JCommander();
    jc.setAcceptUnknownOptions(true);
    jc.addObject(a);
    jc.parse("-a", "foo", "-h", "host");
    Assert.assertEquals(a.host, "host");
    Assert.assertEquals(jc.getUnknownOptions(), Lists.newArrayList("-a", "foo"));
  }

  /**
   * GITHUB-137.
   */
  public void listArgShouldBeCleared() {
    class Args {
      @Parameter(description = "[endpoint]")
      public List<String> endpoint = Lists.newArrayList("prod");
    }
    Args a = new Args();
    new JCommander(a, new String[] { "dev" });
    Assert.assertEquals(a.endpoint, Lists.newArrayList("dev"));
  }

  public void dashDashParameter() {
    class Arguments {
        @Parameter(names = { "-name" })
        public String name;
        @Parameter
        public List<String> mainParameters;
    }

    Arguments a = new Arguments();
    new JCommander(a, new String[] {
        "-name", "theName", "--", "param1", "param2"}
    );
    Assert.assertEquals(a.name, "theName");
    Assert.assertEquals(a.mainParameters.size(), 2);
    Assert.assertEquals(a.mainParameters.get(0), "param1");
    Assert.assertEquals(a.mainParameters.get(1), "param2");
  }

  public void dashDashParameter2() {
    class Arguments {
        @Parameter(names = { "-name" })
        public String name;
        @Parameter
        public List<String> mainParameters;
    }

    Arguments a = new Arguments();
    new JCommander(a, new String[] {
        "param1", "param2", "--", "param3", "-name", "theName"}
    );
    Assert.assertNull(a.name);
    Assert.assertEquals(a.mainParameters.size(), 5);
    Assert.assertEquals(a.mainParameters.get(0), "param1");
    Assert.assertEquals(a.mainParameters.get(1), "param2");
    Assert.assertEquals(a.mainParameters.get(2), "param3");
    Assert.assertEquals(a.mainParameters.get(3), "-name");
    Assert.assertEquals(a.mainParameters.get(4), "theName");
  }

  @Test(enabled = false)
  public static void main(String[] args) throws Exception {
    new JCommanderTest().enumArgsFail();
//    class A {
//      @Parameter(names = "-short", required = true)
//      List<String> parameters;
//
//      @Parameter(names = "-long", required = true)
//      public long l;
//    }
//    A a = new A();
//    new JCommander(a).parse();
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
