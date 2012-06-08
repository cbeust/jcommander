/**
 * Copyright (c) Zachary Kurmas 2011
 *
 */
package com.beust.jcommander;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Zachary Kurmas
 */
// Created  11/14/11 at 9:20 PM
// (C) Zachary Kurmas 2011

public class JCommanderWithAbbreviationsTest {

  public static class OptionSet1 {
    @Parameter(names = "--alpha")
    private int alpha;

    @Parameter(names = {"--beta", "--gamma", "--triskaidekaphobia"})
    private String beta;

    @Parameter(names = "--bellamy")
    public boolean bellamy;


    public int getAlpha() {
      return alpha;
    }

    public String getBeta() {
      return beta;
    }
  }

  public static class OptionSet1WithMain extends OptionSet1 {
    @Parameter
    private List<String> theRest;
  }

  @Parameters(optionPrefixes = "^")
  public static class OptionSet2 {
    @Parameter(names = "^aleph")
    private int aleph;

    @Parameter(names = {"^beit", "^veit", "^triskaidekaphobia"})
    private String beit;

    @Parameter(names = "^better")
    public boolean better;

    public int getAleph() {
      return aleph;
    }

    public String getBeit() {
      return beit;
    }
  }

  @Parameters(optionPrefixes = "^")
  public static class OptionSet2WithMain extends OptionSet2 {
    @Parameter
    private List<String> everythingElse;
  }


  public static class OptionSet3 {
    @Parameter(names = "--alpha", required = true)
    private int alpha;

    @Parameter(names = {"--beta", "--gamma", "--triskaidekaphobia"})
    private String beta;

    @Parameter(names = "--bellamy")
    public boolean bellamy;

    public int getAlpha() {
      return alpha;
    }

    public String getBeta() {
      return beta;
    }
  }

  public static class OptionSet4 {
    @Parameter(names = "-v")
    private boolean verbose;
  }

  //
  // parseWithAbbreviations -- basic behavior
  //


  @Test
  public void parseWithAbbreviationsHandlesFullOptionNames() throws Throwable {
    String[] args = {"--beta", "bs", "--alpha", "14", "--bellamy"};
    OptionSet1 opt = new OptionSet1();
    JCommander jc = new JCommander(opt);
    jc.parseWithAbbreviations(true, args);
    Assert.assertEquals(opt.getBeta(), "bs");
    Assert.assertEquals(opt.getAlpha(), 14);
    Assert.assertTrue(opt.bellamy);
  }

  @Test
  public void parseWithAbbreviationsHandlesFullOptionNames_differentPrefix() throws Throwable {
    String[] args = {"^beit", "bs", "^aleph", "14", "^better"};
    OptionSet2 opt = new OptionSet2();
    JCommander jc = new JCommander(opt);
    jc.parseWithAbbreviations(true, args);
    Assert.assertEquals(opt.getBeit(), "bs");
    Assert.assertEquals(opt.getAleph(), 14);
    Assert.assertTrue(opt.better);
  }

  @Test
  public void parseWithAbbreviationsHandlesAbbreviatedOptionNames() throws Throwable {
    String[] args = {"--bet", "bs", "--a", "14", "--bel"};
    OptionSet1 opt = new OptionSet1();
    JCommander jc = new JCommander(opt);
    jc.parseWithAbbreviations(true, args);
    Assert.assertEquals(opt.getBeta(), "bs");
    Assert.assertEquals(opt.getAlpha(), 14);
    Assert.assertTrue(opt.bellamy);
  }

  @Test
  public void parseWithAbbreviationsHandlesAbbreviatedOptionNames_differentPrefix() throws Throwable {
    String[] args = {"^bei", "bs", "^a", "14", "^bet"};
    OptionSet2 opt = new OptionSet2();
    JCommander jc = new JCommander(opt);
    jc.parseWithAbbreviations(true, args);
    Assert.assertEquals(opt.getBeit(), "bs");
    Assert.assertEquals(opt.getAleph(), 14);
    Assert.assertTrue(opt.better);
  }

  //
  // parseWithAbbreviations -- Error detection
  //


  @Test(expectedExceptions = ParameterException.class)
  public void parseWithAbbreviationsThrowsExceptionIfParameterNotRecognized() throws Throwable {
    String[] args = {"--noSuchParam"};
    OptionSet1 opt = new OptionSet1();

    JCommander jc = new JCommander(opt);
    jc.parseWithAbbreviations(args);
  }

  @Test(expectedExceptions = ParameterException.class)
  public void parseWithAbbreviationsThrowsExceptionIfAbbreviationIsTooShort() throws Throwable {
    String[] args = {"--be", "bs"};
    OptionSet1 opt = new OptionSet1();
    JCommander jc = new JCommander(opt);
    jc.parseWithAbbreviations(true, args);
  }

  @Test
  public void parseWithAbbreviationsThrowsExceptionIfAbbreviationIsTooShort_differentPrefix() throws Throwable {
    String[] args = {"^be", "bs"};
    OptionSet2 opt = new OptionSet2();
    JCommander jc = new JCommander(opt);
    try {
      jc.parseWithAbbreviations(true, args);
      Assert.fail("ParamterException should be thrown because ^be is not a unique prefix");
    } catch (ParameterException e) {
      // If the exception messge contains "main parameter", it means that ^be was not recognized as a parameter.
      Assert.assertFalse(e.getMessage().contains("main parameter"), "Prefix not correctly recognized");
      Assert.assertTrue(e.getMessage().contains("Unknown"));
    }
  }

  @Test
  public void parseWithAbbreviationsThrowsExceptionIfWrongPrefixUsed() {
    String[] args = {"=be", "bs"};
    OptionSet2 opt = new OptionSet2();
    JCommander jc = new JCommander(opt);
    try {
      jc.parseWithAbbreviations(true, args);
      Assert.fail("ParamterException should be thrown because = is the wrong prefix");
    } catch (ParameterException e) {
      Assert.assertTrue(e.getMessage().contains("main parameter"));
      Assert.assertFalse(e.getMessage().contains("Unknown"));
    }
  }


  @Test(expectedExceptions = ParameterException.class)
  public void parseWithAbbreviationsThrowsExceptionIfParameterTooLong() throws Throwable {
    String[] args = {"--alphax"};
    OptionSet1 opt = new OptionSet1();

    JCommander jc = new JCommander(opt);
    jc.parseWithAbbreviations(true, args);
  }

  //
  // parseWithAbbreviations -- validation
  //

  @Test
  public void parseWithAbbreviationsValidatesByDefault() {
    String[] args = {"--bet", "bs"};
    OptionSet3 opt = new OptionSet3();
    JCommander jc = new JCommander(opt);
    try {
      jc.parseWithAbbreviations(args);
      Assert.fail("Expected exception to be thrown complaining about lack of required option");
    } catch (ParameterException e) {
      String actual = e.getMessage();
      Assert.assertTrue(actual.contains("option is required"), "Exception should mention parameter is required.");
      Assert.assertTrue(actual.contains("--alpha"), "Exception should specifically mention required parameter.");
    }
  }

  @Test
  public void parseWithAbbreviationsCanChooseNotToValidate() {
    String[] args = {"--bet", "bs"};
    OptionSet3 opt = new OptionSet3();
    JCommander jc = new JCommander(opt);
    jc.parseWithAbbreviations(false, args);
    Assert.assertEquals(opt.getBeta(), "bs");
    Assert.assertEquals(opt.getAlpha(), 0);
    Assert.assertFalse(opt.bellamy);
  }

  //
  // parseWithAbbreviations -- commands
  //

  @Test
  public void parseWithAbbreviationsWorksWithCommands() {

    OptionSet4 os4 = new OptionSet4();
    OptionSet1WithMain os1 = new OptionSet1WithMain();
    OptionSet2 os2 = new OptionSet2();

    JCommander jc = new JCommander(os4);
    jc.addCommand("c1", os1);
    jc.addCommand("c2", os2);

    String[] args = {"-v", "c1", "--beta", "bs", "--alpha", "14", "--bellamy", "^bei", "zzg", "^a", "19", "^bet"};
    jc.parseWithAbbreviations(true, args);
    Assert.assertTrue(os4.verbose);
    Assert.assertEquals(os1.getBeta(), "bs");
    Assert.assertEquals(os1.getAlpha(), 14);
    Assert.assertTrue(os1.bellamy);
    Assert.assertEquals(os1.theRest, java.util.Arrays.asList(new String[]{"^bei", "zzg", "^a", "19", "^bet"}));
  }

  @Test
  public void parseWithAbbreviationsWorksWithCommands_differentPrefix() {

    OptionSet4 os4 = new OptionSet4();
    OptionSet1WithMain os1 = new OptionSet1WithMain();
    OptionSet2WithMain os2 = new OptionSet2WithMain();

    JCommander jc = new JCommander(os4);
    jc.addCommand("c1", os1);
    jc.addCommand("c2", os2);

    String[] args = {"-v", "c2", "--beta", "bs", "--alpha", "14", "--bellamy", "^bei", "zzg", "^a", "19", "^bet"};
    jc.parseWithAbbreviations(true, args);
    Assert.assertTrue(os4.verbose);
    Assert.assertEquals(os2.getBeit(), "zzg");
    Assert.assertEquals(os2.getAleph(), 19);
    Assert.assertTrue(os2.better);
    Assert.assertEquals(os2.everythingElse, java.util.Arrays.asList(new String[]{"--beta", "bs", "--alpha", "14", "--bellamy"}));
  }
}
