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
    jc.getUsageFormatter().usage("commit", out);
    patternMatchesTimes("commit\\(ci,cmt\\)", out.toString(), 1);

    out = new StringBuilder();
    jc.getUsageFormatter().usage("ci", out);
    patternMatchesTimes("commit\\(ci,cmt\\)", out.toString(), 1);

    out = new StringBuilder();
    jc.getUsageFormatter().usage("cmt", out);
    patternMatchesTimes("commit\\(ci,cmt\\)", out.toString(), 1);
  }

  @Test
  public void usageDisplaysCommandWithAliasesOnlyOnce() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit, "ci", "cmt");
    StringBuilder out = new StringBuilder();
    jc.getUsageFormatter().usage(out);
    // The usage should display this string twice: one as the command name
    // and one after Usage:
    patternMatchesTimes("commit\\(ci,cmt\\)", out.toString(), 2);
  }

  /**
   * Visually test the formatting for "prettiness"
   */
  @Test(enabled = false, description = "TODO: test the output instead of displaying it")
  public void formattingLooksNice(){
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandAdd add = new CommandAdd();
    jc.addCommand("add", add, "a");
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit, "ci", "cmt");
    StringBuilder sb = new StringBuilder();
    jc.getUsageFormatter().usage(sb);
    System.out.println("--- usage() formatting ---");
    System.out.println(sb.toString());

    sb = new StringBuilder();
    jc.getUsageFormatter().usage("commit", sb);
    System.out.println("--- usage('commit') formatting ---");
    System.out.println(sb.toString());
  }

  private void patternMatchesTimes(String pattern, String input, int times) {
    Matcher m = Pattern.compile(pattern).matcher(input);
    int matches = 0;
    while (m.find()) matches++;
    Assert.assertEquals(matches, times);
  }
}
