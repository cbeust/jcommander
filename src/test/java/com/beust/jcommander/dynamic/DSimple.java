package com.beust.jcommander.dynamic;

import com.beust.jcommander.DynamicParameter;

import org.testng.collections.Maps;

import java.util.Map;

public class DSimple {

  @DynamicParameter(names = "-D")
  public Map<String, String> params = Maps.newHashMap();
}
