package com.beust.jcommander.converters;

import java.util.Arrays;
import java.util.List;

public class CommaParameterSplitter implements IParameterSplitter {

  @Override
  public List<String> split(final String value) {
    return getJsonType(value.trim()) ? Arrays.asList(value) : Arrays.asList(value.split(","));
  }


  /**
   * Check if the parameter is in Json format.
   *
   * @param arg the split parameter
   * @return {@code true} if {@code String} is in Json format
   */
  public static boolean getJsonType(final String arg) {
    final char[] chars = arg.toCharArray();
    return chars[0] == '{' && chars[chars.length-1] == '}' || chars[0] == '[' && chars[chars.length-1] == ']'
        ? true : false;
  }
}
