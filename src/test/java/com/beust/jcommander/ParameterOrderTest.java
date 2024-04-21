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

  private static class ManualCategoryOrder1 {
    @Parameter(names = "--arg_a", category = "Category 1")
    public boolean isA;
    @Parameter(names = "--arg_b", category = "Category 2")
    public boolean isB;
    @Parameter(names = "--arg_c", category = "Category 1")
    public boolean isC;
    @Parameter(names = "--arg_d", category = "Category 2")
    public boolean isD;
  }

  @Test
  public void testCategoryOrder1() {
    testOrder(new ManualCategoryOrder1(), "--arg_a","--arg_c","--arg_b", "--arg_d");
  }

  private static class ManualCategoryOrder2 {
    @Parameter(names = "--arg_a", category = "Category 1", order = 2)
    public boolean isA;
    @Parameter(names = "--arg_b", category = "Category 2", order = 2)
    public boolean isB;
    @Parameter(names = "--arg_c", category = "Category 1", order = 1)
    public boolean isC;
    @Parameter(names = "--arg_d", category = "Category 2", order = 1)
    public boolean isD;
  }

  @Test
  public void testCategoryOrder2() {
    testOrder(new ManualCategoryOrder2(), "--arg_c","--arg_a","--arg_d", "--arg_b");
  }

  public void testOrder(Object cmd, String ... expected) {
    JCommander commander = new JCommander(cmd);

    StringBuilder out = new StringBuilder();
    commander.getUsageFormatter().usage(out);
    String output = out.toString();
    List<String> order = new ArrayList<>();
    for (String line : output.split(System.getProperty("line.separator"))) {
      String trimmed = line.trim();
      if (!trimmed.contains(":")) {
        order.add(trimmed);
      }
    }
    Assert.assertEquals(order, Arrays.asList(expected));
  }

  private static class WithoutOrder {
    @Parameter(names = "--arg_b")
    public boolean isB;
    @Parameter(names = "--arg_c")
    public boolean isC;
    @Parameter(names = "--arg_a")
    public boolean isA;
  }

  @Test
  public void parametersWithoutOrder() {
    testOrder(new WithoutOrder(), "--arg_a", "--arg_b", "--arg_c");
  }

  private static class WithSameOrder {
    @Parameter(order=0, names = "--arg_b")
    public boolean isB;
    @Parameter(order=0, names = "--arg_c")
    public boolean isC;
    @Parameter(order=0, names = "--arg_a")
    public boolean isA;
  }

  @Test
  public void parametersWithSameOrder() {
    testOrder(new WithSameOrder(), "--arg_a", "--arg_b", "--arg_c");
  }
}
