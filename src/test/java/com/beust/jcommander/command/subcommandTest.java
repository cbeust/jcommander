package test;

import com.beust.jcommander.*;
import java.util.*;
import org.testng.annotations.Test;
import org.testng.Assert;

class testClass1 {
  @Parameter(names = "testCommand",description = "same name test")
  public String result = "null";

  @Parameter(names = "mainTestCommand",description = "main command test")
  public int mainResult = -1;
}

class testClass2 {
  @Parameter(names = "testCommand",description = "test 2 over~!")
  public String result = "null";

  @Parameter(names = "subTestCommand",description = "sub command test")
  public int subResult = -1;
}


////CS304 (manually written) Issue link:https://github.com/cbeust/jcommander/issues/431
public class subcommandTest {

  @Test
  private void subCommandOnlyTest() {
    //String[] args = new String[]{"command1", "mainTestCommand", "1", "command1", "sub", "subTestCommand", "2"};
    //String[] args = new String[]{"command1", "mainTestCommand", "1"};
    String[] args = new String[]{"command1", "sub", "subTestCommand", "2"};
    JCommander jc = new JCommander(this);
    testClass1 mainCommand = new testClass1();
    testClass2 subCommand = new testClass2();
    jc.addCommand("command1", mainCommand);
    jc.addSubcommand("sub", subCommand, "command1");
    jc.parse(args);

    Assert.assertTrue(mainCommand.mainResult == -1, "the result of main command is:" + mainCommand.mainResult + " but not -1");
    Assert.assertTrue(subCommand.subResult == 2, "the result of sub command is:" + subCommand.subResult + " but not 2");
  }

  @Test
  private void mainCommandOnlyTest() {
    String[] args = new String[]{"command1", "mainTestCommand", "1"};
    JCommander jc = new JCommander(this);
    testClass1 mainCommand = new testClass1();
    testClass2 subCommand = new testClass2();
    jc.addCommand("command1", mainCommand);
    jc.addSubcommand("sub", subCommand, "command1");
    jc.parse(args);

    Assert.assertTrue(mainCommand.mainResult == 1, "the result of main command is:" + mainCommand.mainResult + " but not 1");
    Assert.assertTrue(subCommand.subResult == -1, "the result of sub command is:" + subCommand.subResult + " but not -1");
  }

  @Test
  private void sameNameParamentTestSub() {
    String[] args = new String[]{"command1", "sub", "testCommand", "test over"};
    JCommander jc = new JCommander(this);
    testClass1 mainCommand = new testClass1();
    testClass2 subCommand = new testClass2();
    jc.addCommand("command1", mainCommand);
    jc.addSubcommand("sub", subCommand, "command1");
    jc.parse(args);

    Assert.assertTrue(mainCommand.result == "null", "the result of main command is:" + mainCommand.result + " but not null");
    Assert.assertTrue(subCommand.result == "test over", "the result of sub command is:" + subCommand.result + " but not test over");
  }

  @Test
  private void sameNameParamentTestMain() {
    String[] args = new String[]{"command1", "testCommand", "test over"};
    JCommander jc = new JCommander(this);
    testClass1 mainCommand = new testClass1();
    testClass2 subCommand = new testClass2();
    jc.addCommand("command1", mainCommand);
    jc.addSubcommand("sub", subCommand, "command1");
    jc.parse(args);

    Assert.assertTrue(mainCommand.result == "test over", "the result of main command is:" + mainCommand.result + " but not test over");
    Assert.assertTrue(subCommand.result == "null", "the result of sub command is:" + subCommand.result + " but not null");
  }
}