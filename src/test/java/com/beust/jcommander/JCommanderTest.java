package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.Arrays;
import java.util.List;

public class JCommanderTest {
  @Parameter
  public List<String> parameters = Lists.newArrayList();

  @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
  public Integer verbose = 1;

  @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
  public String groups;

  @Parameter(names = "-debug", description = "Debug mode")
  public boolean debug = false;

  @Test
  public void simpleArgs() {
    JCommanderTest jct = new JCommanderTest();
    String[] argv = { "-log", "2", "-groups", "unit", "a", "b", "c" };
    new JCommander(jct, argv);

    System.out.println("Verbose:" + verbose);
    Assert.assertEquals(jct.verbose.intValue(), 2);
    Assert.assertEquals(jct.groups, "unit");
    Assert.assertEquals(jct.parameters, Arrays.asList("a", "b", "c"));
  }

  /**
   * Make sure that if there are args with multiple names (e.g. "-log" and "-verbose"),
   * the usage will only display it once.
   */
  @Test
  public void repeatedArgs() {
    JCommanderTest jct = new JCommanderTest();
    String[] argv = { "-log", "2" };
    JCommander jc = new JCommander(jct, argv);
    Assert.assertEquals(jc.getParameters().size(), 3);
  }

  public static void main(String[] args) {
    new JCommanderTest().repeatedArgs();
  }
}
