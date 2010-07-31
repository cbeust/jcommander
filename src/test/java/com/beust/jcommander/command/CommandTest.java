package com.beust.jcommander.command;

import com.beust.jcommander.JCommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

public class CommandTest {
  @Test
  public void commandTest1() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandAdd add = new CommandAdd();
    jc.addCommand("add", add);
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit);
    jc.parse("add", "-i", "A.java");

    Assert.assertEquals(jc.getParsedCommand(), "add");
    Assert.assertEquals(add.interactive.booleanValue(), true);
    Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
  }

  @Test
  public void commandTest2() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandAdd add = new CommandAdd();
    jc.addCommand("add", add);
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit);
    jc.parse("-v", "commit", "--amend", "--author=cbeust", "A.java", "B.java");

    Assert.assertTrue(cm.verbose);
    Assert.assertEquals(jc.getParsedCommand(), "commit");
    Assert.assertTrue(commit.amend);
    Assert.assertEquals(commit.author, "cbeust");
    Assert.assertEquals(commit.files, Arrays.asList("A.java", "B.java"));
  }

}
