package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class VariableArityTest {

  public static class ModelGenerationConfig {

    @Parameter(names = { "-m", "--matrixData" }, variableArity = true,
        description = "File containing a list of instances and their runtimes on various configurations", required = false)
    public List<String> modelMatrixFile = new LinkedList<>();

    @Parameter(names = { "-f", "--featureData" }, variableArity = true,
        description = "File containing a list of instances and their corresponding features", required = true)
    public List<String> featureFile = new LinkedList<>();

    @Parameter(names = { "-c", "--configData" }, variableArity = true,
        description = "File containing a list of configuration parameter values")
    public List<String> configFile = new LinkedList<>();

    @Parameter(names = { "-o", "--outputFile" },
        description = "File to output the resulting data to. Defaults to ./matrix-generation.zip", required = false)
    public String outputFile = "matrix-generation.zip";

    @Parameter(names = { "--seed" }, description = "Seed used for PRNG [0 means don't use a Seed]")
    public long seed = 0;

    @Parameter(names = "-J", description = "Parameters to be passed to child process", variableArity = true)
    public List<String> j;

    public void print() {
      System.out.println("modelMatrixFile: " + modelMatrixFile);
      System.out.println("featureData: " + featureFile);
      System.out.println("configFile: " + configFile);
      System.out.println("output:  " + outputFile);
      System.out.println("seed: " + seed);

    }
  }

  @Test
  public void verifyVariableArity() {
    String input = "-m foo --seed 1024 -J --compilation_level WHITESPACE_ONLY --language_in=ECMASCRIPT5 -bar baz -faz -c foo -o foo -f foo -J --more-options";

    String[] split = input.split("\\s+");

    ModelGenerationConfig config = new ModelGenerationConfig();
    JCommander com = new JCommander(config);
    com.setProgramName("modelgen");

    com.parse(split);

    Assert.assertNotEquals(config.seed, 0);
    Assert.assertEquals(config.modelMatrixFile, Arrays.asList("foo"));
    Assert.assertEquals(config.featureFile, Arrays.asList("foo"));
    Assert.assertEquals(config.seed, 1024);
    Assert.assertEquals(config.configFile, Arrays.asList("foo"));
    Assert.assertEquals(config.outputFile, "foo");
    Assert.assertEquals(config.j, Arrays.asList("--compilation_level", "WHITESPACE_ONLY", "--language_in=ECMASCRIPT5", "-bar", "baz", "-faz", "--more-options"));
  }

  @Parameters(separators = "=")
  public static class EqualsModelGenerationConfig extends ModelGenerationConfig {
  }

  @Test
  public void verifyVariableArity_unknownOptions() {
    String[] input =
        {"-m", "foo", "--seed", "1024", "-c=foo", "bar", "-f", "foo", "-o=out.txt", "--extra"};
    EqualsModelGenerationConfig config = new EqualsModelGenerationConfig();
    JCommander com = new JCommander(config);
    com.setProgramName("modelgen");
    com.setAcceptUnknownOptions(true);

    com.parse(input);

    Assert.assertNotEquals(config.seed, 0);
    Assert.assertEquals(config.modelMatrixFile, Arrays.asList("foo"));
    Assert.assertEquals(config.featureFile, Arrays.asList("foo"));
    Assert.assertEquals(config.seed, 1024);
    Assert.assertEquals(config.outputFile, "out.txt");
    Assert.assertEquals(config.configFile, Arrays.asList("foo", "bar"));
  }

  public static void main(String[] args) {
    new VariableArityTest().verifyVariableArity();
    new VariableArityTest().verifyVariableArity_unknownOptions();
  }
}