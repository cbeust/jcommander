package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>Test that parameter order specified via order attributes is respected</p>
 */
public class ParameterOrderTest {

  private static class ManualOrder1 {
    @Parameter(order=1, names = "--arg_b")
    public boolean isB;
    @Parameter(order=0, names = "--arg_a")
    public boolean isA;
    @Parameter(order=2, names = "--arg_c")
    public boolean isC;
  }

  @Test
  public void testOrder1() {
    testOrder(new ManualOrder1(), "--arg_a","--arg_b","--arg_c");
  }

  private static class ManualOrder2 {
    @Parameter(order=1, names = "--arg_b")
    public boolean isZ;
    @DynamicParameter(order=0, names = "--arg_a")
    public Map<String,String> mapA;
    @Parameter(order=2, names = "--arg_c")
    public boolean isC;
  }

  @Test
  public void testOrder2() {
    testOrder(new ManualOrder2(), "--arg_a","--arg_b","--arg_c");
  }

  private static class ManualOrder3 {
    @Parameter(order=1, names = "--arg_b")
    public boolean isB;
    @Parameter(order=0, names = "--arg_a")
    public boolean isA;
    @Parameter(names = "--arg_d")
    public boolean isD;
    @Parameter(order=2, names = "--arg_c")
    public boolean isC;
  }

  @Test
  public void testOrder3() {
    testOrder(new ManualOrder3(), "--arg_a","--arg_b","--arg_c", "--arg_d");
  }

  public void testOrder(Object cmd, String ... expected) {
    JCommander commander = new JCommander(cmd);

    StringBuilder out = new StringBuilder();
    commander.getUsageFormatter().usage(out);
    String output = out.toString();
    List<String> order = new ArrayList<>();
    for (String line : output.split("[\\n\\r]+")) {
      String trimmed = line.trim();
      if (!trimmed.contains(":")) {
        order.add(trimmed);
      }
    }
    Assert.assertEquals(order, Arrays.asList(expected));
  }
}
