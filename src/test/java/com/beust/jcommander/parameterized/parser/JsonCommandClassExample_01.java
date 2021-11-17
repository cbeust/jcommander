package com.beust.jcommander.parameterized.parser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * This is an arbitrary class to test Parameter values using JSON annotations instead with 
 * JCommander annotations.
 *
 * @author Tim Gallagher
 */
public class JsonCommandClassExample_01 {

  public static final String PARAM_VERSION = "version";

  /**
   * In this example, the JsonProperty annotation does not include enough values to allow us to
   * indicate a delegated object for JCommander, as the JCommander annotations do.  
   * However, because the object is not a java.lang or other Java supplied objects, we can assumed 
   * it is delegated.
   * 
   * A more precise way to do this, is to introduce new annotation, MyDelegate in this case,
   * to simulate JCommander but does not require JCommander for your low level libraries. 
   * For example, lets say you have a REST component, you could use the JsonProperty and other 
   * JSON annotations for that service, but add a new annotation so that when you want to pull 
   * that service component out and into a command line app, you could use that new Annotation
   * within the context of JCommander.  Here we use a very simple MyDelegate.  But there is no
   * reason why you can't add more data to it.
   */
  @JsonProperty(
    value = "subCommands"
  )
  @MyDelegate
  public final JsonCommandClassExample_02 subCommands = new JsonCommandClassExample_02();

  /**
   * In this example, the JsonProperty does not have any description, but there is a 
   * JsonPropertyDescription annotation that we can take advantage of.
   */
  @JsonProperty(
    value = PARAM_VERSION,
    required = true
  )
  @JsonPropertyDescription("Version of the software to run. eg. \"v38.1.0\"")
  public String version;

}
