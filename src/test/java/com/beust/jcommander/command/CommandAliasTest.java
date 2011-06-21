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

package com.beust.jcommander.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tests command alias functionality
 *
 * @author rodionmoiseev
 */
public class CommandAliasTest {
  @Test
  public void oneCommandWithSingleAlias() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandAdd add = new CommandAdd();
    jc.addCommand("add", add, "a");
    jc.parse("a", "-i", "A.java");

    Assert.assertEquals(jc.getParsedCommand(), "add");
    Assert.assertEquals(jc.getParsedAlias(), "a");
    Assert.assertEquals(add.interactive.booleanValue(), true);
    Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
  }

  @Test
  public void oneCommandWithMultipleAliases_commit_ci() {
    testCommitWithAlias("ci");
  }

  @Test
  public void oneCommandWithMultipleAliases_commit_cmt() {
    testCommitWithAlias("cmt");
  }

  private void testCommitWithAlias(String alias) {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit, "ci", "cmt");
    jc.parse(alias, "--amend", "--author", "jack", "file1.txt");

    Assert.assertEquals(jc.getParsedCommand(), "commit");
    Assert.assertEquals(jc.getParsedAlias(), alias);
    Assert.assertEquals(commit.amend.booleanValue(), true);
    Assert.assertEquals(commit.author, "jack");
    Assert.assertEquals(commit.files, Arrays.asList("file1.txt"));
  }

  @Test
  public void twoCommandsWithAliases() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandAdd add = new CommandAdd();
    jc.addCommand("add", add, "a");
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit, "ci", "cmt");
    jc.parse("a", "-i", "A.java");

    Assert.assertEquals(jc.getParsedCommand(), "add");
    Assert.assertEquals(add.interactive.booleanValue(), true);
    Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
  }

  @Test
  public void clashingAliasesAreNotAllowed() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandAdd add = new CommandAdd();
    jc.addCommand("add", add, "xx");
    CommandCommit commit = new CommandCommit();
    try {
      jc.addCommand("commit", commit, "ci", "xx");
      Assert.fail("Should not be able to register clashing alias 'xx'");
    } catch (ParameterException pe) {
      //Make sure the message mentions that "xx" aliases is already
      //defined for "add" command
      Assert.assertTrue(pe.getMessage().contains("xx"));
      Assert.assertTrue(pe.getMessage().contains("add"));
    }
  }

  @Test
  public void mainCommandReturnsNullsForGetCommandAndGetParsedAlias() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    Assert.assertNull(jc.getParsedCommand());
    Assert.assertNull(jc.getParsedAlias());
  }

  @Test
  public void usageCanBeRetrievedWithBothCommandAndAlias() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit, "ci", "cmt");
    StringBuilder out = new StringBuilder();
    jc.usage("commit", out);
    patternMatchesTimes("commit\\(ci,cmt\\)", out.toString(), 1);

    out = new StringBuilder();
    jc.usage("ci", out);
    patternMatchesTimes("commit\\(ci,cmt\\)", out.toString(), 1);

    out = new StringBuilder();
    jc.usage("cmt", out);
    patternMatchesTimes("commit\\(ci,cmt\\)", out.toString(), 1);
  }

  @Test
  public void usageDisplaysCommandWithAliasesOnlyOnce() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit, "ci", "cmt");
    StringBuilder out = new StringBuilder();
    jc.usage(out);
    patternMatchesTimes("commit\\(ci,cmt\\)", out.toString(), 1);
  }

  @Test
  public void addingAnnotatedCommandSetsUpCommandNameAndAliasesFromAnnotation(){
    @Parameters(commandName = "add")
    class Add{}
    @Parameters(commandName = "commit", commandAliases = {"ci", "cmt"})
    class Commit{}
    JCommander jc = new JCommander(new CommandMain());
    Add add = new Add();
    jc.addCommand(add);
    Assert.assertTrue(jc.getCommands().containsKey("add"));
    Commit cmt = new Commit();
    jc.addCommand(cmt);
    Assert.assertTrue(jc.getCommands().containsKey("commit"));

    StringBuilder out = new StringBuilder();
    jc.usage(out);
    patternMatchesTimes("add", out.toString(), 1);
    patternMatchesTimes("commit\\(ci,cmt\\)", out.toString(), 1);
  }

  @Test
  public void nameGivenToAddCommandTakesPrecedence(){
    @Parameters(commandName = "otherName")
    class Commit{}
    JCommander jc = new JCommander(new CommandMain());
    Commit cmt = new Commit();
    jc.addCommand("commit", cmt);
    //command is registered as commit and not otherName
    Assert.assertTrue(jc.getCommands().containsKey("commit"));
    Assert.assertFalse(jc.getCommands().containsKey("otherName"));
  }

  @Test
  public void aliasesGivenToAddCommandTakePrecedence(){
    @Parameters(commandName = "commit", commandAliases = "c")
    class Commit{}
    JCommander jc = new JCommander(new CommandMain());
    jc.addCommand("commit", new Commit(), "ci", "cmt");
    StringBuilder out = new StringBuilder();
    jc.usage(out);
    patternMatchesTimes("commit\\(ci,cmt\\)", out.toString(), 1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void addingCommandWithoutSpecifyingTheNameOrAnnotationThrowsException(){
    class Unannotated{}
    JCommander jc = new JCommander(new CommandMain());
    jc.addCommand(new Unannotated());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void addingCommandAndNotSpecifyingTheNameThrowsException(){
    @Parameters
    class Unnamed{}
    JCommander jc = new JCommander(new CommandMain());
    jc.addCommand(new Unnamed());
  }

  /**
   * Visually test the formatting for "prettiness"
   */
  @Test
  public void formattingLooksNice(){
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandAdd add = new CommandAdd();
    jc.addCommand("add", add, "a");
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit, "ci", "cmt");
    StringBuilder sb = new StringBuilder();
    jc.usage(sb);
    Assert.assertEquals(sb.toString(),
      "Usage: <main class> [options] [command] [command options]\n" +
      "  Options:\n" +
      "    -v   Verbose mode\n" +
      "         Default: false\n" +
      "  Commands:\n" +
      "    add(a)           Add file contents to the index\n" +
      "    commit(ci,cmt)   Record changes to the repository\n");

    sb = new StringBuilder();
    jc.usage("commit", sb);
    Assert.assertEquals(sb.toString(),
      "Record changes to the repository\n" +
      "Usage: commit(ci,cmt) [options]\n" +
      " List of files\n" +
      "  Options:\n" +
      "        --amend    Amend\n" +
      "                   Default: false\n" +
      "        --author   \n");
  }

  private void patternMatchesTimes(String pattern, String input, int times) {
    Matcher m = Pattern.compile(pattern).matcher(input);
    int matches = 0;
    while (m.find())
      matches++;
    Assert.assertEquals(matches, times);
  }
}
