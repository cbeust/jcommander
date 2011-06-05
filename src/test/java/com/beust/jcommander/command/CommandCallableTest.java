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

import com.beust.jcommander.JCallable;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;

/**
 * @author rodionmoiseev
 */
public class CommandCallableTest {

  @Parameters(commandName = "commit", commandAliases = "ci", commandDescription = "Commit changes")
  class Commit {
    @Parameter(names = "-v")
    private boolean verbose;
    @Parameter(names = {"-m", "--message"}, required = true)
    private String message;
  }

  private Commit cm;
  private boolean callableCalled = false;

  @BeforeMethod
  public void setUp(){
    cm = new Commit();
    callableCalled = false;
  }

  @Test
  public void scenarioUsingJCallable() {
    testWithCallable(new JCallable<Commit>() {
      public void call(Commit parsedOpts) throws Exception {
        callableCalled = true;
        Assert.assertEquals(parsedOpts, cm);
        Assert.assertEquals(parsedOpts.message, "Commit message");
        Assert.assertTrue(parsedOpts.verbose);
      }
    });
  }

  @Test
  public void scenarioUsingRunnables() {
    testWithCallable(new Runnable() {
      public void run() {
        callableCalled = true;
        Assert.assertEquals(cm.message, "Commit message");
        Assert.assertTrue(cm.verbose);
      }
    });
  }

  @Test
  public void scenarioUsingCallable() {
    testWithCallable(new Callable<Void>() {
      public Void call() {
        callableCalled = true;
        Assert.assertEquals(cm.message, "Commit message");
        Assert.assertTrue(cm.verbose);
        return null;
      }
    });
  }

  private void testWithCallable(Object callable){
    JCommander jc = new JCommander();
    jc.addCommand(cm, callable);
    jc.parse("ci", "-v", "--message", "Commit message");
    Assert.assertTrue(callableCalled);
  }

  @Test
  public void callableDoesNotGetCalledIfTheCommandIsNotParsed() throws Exception {
    CommandMain main = new CommandMain();
    JCommander jc = new JCommander(main);
    jc.addCommand(cm, new JCallable<Commit>() {
      public void call(Commit parsedOpts) throws Exception {
        Assert.fail("Should not be called");
      }
    });
    jc.parseChecked("-v");
    Assert.assertTrue(main.verbose);
  }

  /*
   * The expected behaviour of a callable on the main object is
   * not very intuitive, and is therefore not supported.
   *
   * One possible behaviour would be to execute the main-callable
   * only when no command has been parsed. Alternatively, execute
   * it whenever no other callable has been called (a kind of fallback).
   * Either way, the usefulness of these two specs is debatable.
   */
  @Test
  public void callableOnTheMainJCommanderObjectDoesNothing(){
    JCommander jc = new JCommander(new CommandMain());
    jc.setCallable(new Runnable() {
      public void run() {
        Assert.fail("Should not be called. Spec not decided.");
      }
    });
    jc.parse("-v");
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = ".*UnsupportedCallable.*")
  public void unsupportedCallableTypesFailFast() {
    class UnsupportedCallable {
      public void run() {
      }
    }

    JCommander jc = new JCommander();
    jc.addCommand(new Commit(), new UnsupportedCallable());
  }
}
