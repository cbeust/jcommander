package com.beust.jcommander.parameterized.parser;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.Parameterized;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;



/**
 * Tests the Parameterized parser for substituting the original parser looking go @Parameter
 * but can be replaced to look for other annotations that define run-time parameters.
 *
 * @author Tim Gallagher
 */
public class ParameterizedParserTest {
  
  static final String EXPECTED_VERSION = "v380.1.0";
  static final int EXPECTED_STACK_LEVEL = 5;
  static final String EXPECTED_LOG_LEVEL = "DEBUG";
  static final String[] ARGS = new String[] { "version", EXPECTED_VERSION, 
    "stackLevel", Integer.toString(EXPECTED_STACK_LEVEL), 
    "logLevel", EXPECTED_LOG_LEVEL };

  static final Map<String, Boolean> EXPECTED_MAP = new HashMap<>();
  static {
    EXPECTED_MAP.put(StandardCommandClassExample_01.PARAM_VERSION, Boolean.FALSE);
    EXPECTED_MAP.put(StandardCommandClassExample_02.PARAM_STACK_LEVEL, Boolean.FALSE);
    EXPECTED_MAP.put(StandardCommandClassExample_02.PARAM_LOG_LEVEL, Boolean.FALSE);
  }

  public ParameterizedParserTest() {
  }

  /**
   * This test ensures that the additional IParameterizedParser does not break
   * current functionality.
   */
  @Test
  public void standardParameterizedParsingTest() {
    StandardCommandClassExample_01 commandOptions = new StandardCommandClassExample_01();
    
    JCommander jcommander = new JCommander(commandOptions);
    
    testFields(jcommander, EXPECTED_MAP);

    jcommander.parse(ARGS);
    Assert.assertTrue(EXPECTED_VERSION.equals(commandOptions.version), "Version is not " + EXPECTED_VERSION);
    Assert.assertTrue(EXPECTED_STACK_LEVEL == commandOptions.subCommands.stackLevel, "Stack level field is not" + EXPECTED_STACK_LEVEL);
    Assert.assertTrue(EXPECTED_LOG_LEVEL.equals(commandOptions.subCommands.loggingLevel), "Log level is not " + EXPECTED_LOG_LEVEL);
  }
  
  @Test
  public void jsonParameterizedParsingTest() {
    JsonCommandClassExample_01 commandOptions = new JsonCommandClassExample_01();
    
    JCommander jcommander = new JCommander();
    jcommander.setParameterizedParser(new JsonAnnotationParameterizedParser());
    jcommander.addObject(commandOptions);
    
    testFields(jcommander, EXPECTED_MAP);

    jcommander.parse(ARGS);
    Assert.assertTrue(EXPECTED_VERSION.equals(commandOptions.version), "Version is not " + EXPECTED_VERSION);
    Assert.assertTrue(EXPECTED_STACK_LEVEL == commandOptions.subCommands.stackLevel, "Stack level field is not" + EXPECTED_STACK_LEVEL);
    Assert.assertTrue(EXPECTED_LOG_LEVEL.equals(commandOptions.subCommands.loggingLevel), "Log level is not " + EXPECTED_LOG_LEVEL);
  }
  
  
  public void testFields(JCommander jcommander, Map<String, Boolean> expectedMap) {
    Map<Parameterized,ParameterDescription> fields = jcommander.getFields();

    Collection<ParameterDescription> paramDescs = fields.values();
    for (ParameterDescription paramDesc : paramDescs) {
      Assert.assertTrue(expectedMap.containsKey(paramDesc.getNames()), "Parameter not in the expected: " + paramDesc.getNames());
      expectedMap.put(paramDesc.getNames(), Boolean.TRUE);
    }
    
    Assert.assertTrue(expectedMap.containsKey(StandardCommandClassExample_01.PARAM_VERSION), "Version field not found");
    Assert.assertTrue(expectedMap.containsKey(StandardCommandClassExample_02.PARAM_STACK_LEVEL), "Stack level field not found");
    Assert.assertTrue(expectedMap.containsKey(StandardCommandClassExample_02.PARAM_LOG_LEVEL), "Log level field not found");
    
    
  }  
}
