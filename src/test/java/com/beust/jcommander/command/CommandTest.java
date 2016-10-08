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

import com.beust.jcommander.ArgsValidate2;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;

public class CommandTest {
  @Test
  public void namedCommandTest1() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    NamedCommandAdd add = new NamedCommandAdd();
    jc.addCommand(add);
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit);
    jc.parse("add", "-i", "A.java");

    Assert.assertEquals(jc.getParsedCommand(), "add");
    Assert.assertEquals(add.interactive.booleanValue(), true);
    Assert.assertEquals(add.patterns, Arrays.asList("A.java"));
  }

  @Test(expectedExceptions = ParameterException.class)
  public void shouldComplainIfNoAnnotations() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandAdd add = new CommandAdd();
    jc.addCommand(add);
  }

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

    @Test
    public void hiddenCommandTest() {
        CommandMain cm = new CommandMain();
        JCommander jc = new JCommander(cm);
        CommandAdd add = new CommandAdd();
        jc.addCommand("add", add);
        CommandHidden hidden = new CommandHidden();
        jc.addCommand("hidden", hidden);
        jc.parse("hidden", "-i", "A.java");

        Assert.assertEquals(jc.getParsedCommand(), "hidden");
        Assert.assertEquals(hidden.interactive.booleanValue(), true);
        Assert.assertEquals(hidden.patterns, Arrays.asList("A.java"));

        jc.setProgramName("TestCommander");
        StringBuilder out = new StringBuilder();
        jc.usage(out);

        Assert.assertTrue(out.toString().contains("add      Add file contents to the index"));
        Assert.assertFalse(out.toString().contains("hidden      Hidden command to add file contents to the index"));
    }

  @Test
  public void noParametersAnnotationOnCommandTest() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandNoParametersAnnotation noParametersAnnotation = new CommandNoParametersAnnotation();
    jc.addCommand("no-annotation", noParametersAnnotation);

    jc.setProgramName("TestCommander");
    StringBuilder out = new StringBuilder();
    jc.usage(out);

    Assert.assertTrue(out.toString().contains("no-annotation"));
  }

  @Test
  public void noTrailingSpaceInUsageTest() {
    CommandMain cm = new CommandMain();
    JCommander jc = new JCommander(cm);
    CommandAdd add = new CommandAdd();
    jc.addCommand("add", add);
    CommandCommit commit = new CommandCommit();
    jc.addCommand("commit", commit);
    jc.parse("-v", "commit", "--amend", "--author=cbeust", "A.java", "B.java");
    StringBuilder out = new StringBuilder();
    jc.usage(out);
    String firstLine = out.toString().split("\n")[0];
    Assert.assertFalse(firstLine.endsWith(" "), "Usage should not have trailing spaces");
  }

  @Test(expectedExceptions = ParameterException.class)
  public void validateSubCommand() throws Exception {
    JCommander jc = new JCommander(new CommandMain());
    final ArgsValidate2 sub = new ArgsValidate2();
    sub.template = null;
    jc.addCommand("sub", sub);
    jc.parse("sub", "-template", "foo");
  }

  @Test
  public void doNotValidateSubCommand() throws Exception {
    JCommander jc = new JCommander(new CommandMain());
    final ArgsValidate2 sub = new ArgsValidate2();
    sub.template = null;
    jc.addCommand("sub", sub);
    jc.parseWithoutValidation("sub", "-template", "foo");
    Assert.assertEquals(sub.template, new File("foo"));

  }

  public static void main(String[] args) {
    new CommandTest().shouldComplainIfNoAnnotations();
  }
}
