package com.beust.jcommander;

import java.util.SortedSet;
import java.util.TreeSet;

public class SetConverter implements IStringConverter<SortedSet<Integer>> {
 
  public SortedSet<Integer> convert(String value) {
    SortedSet<Integer> set = new TreeSet<>();
    String[] values = value.split(",");
    for (String num : values) {
      set.add(Integer.parseInt(num));
    }
    return set;
  }
}
