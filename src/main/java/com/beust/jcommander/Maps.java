package com.beust.jcommander;

import java.util.HashMap;
import java.util.Map;

public class Maps {

  public static <K, V> Map<K,V> newHashMap() {
    return new HashMap<K, V>();
  }
}
