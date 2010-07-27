package com.beust.jcommander;

/**
 * Allows the specification of default values.
 * 
 * @author cbeust
 */
public interface IDefaultProvider {

  /**
   * @param optionName The name of the option as specified in the names() attribute
   * of the @Parameter option (e.g. "-file").
   * 
   * @return the default value for this option.
   */
  String getDefaultValueFor(String optionName);
}
