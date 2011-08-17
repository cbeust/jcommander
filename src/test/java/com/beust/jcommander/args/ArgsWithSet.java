package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.SetConverter;

import java.util.SortedSet;

public class ArgsWithSet {
  @Parameter(names = "-s", converter = SetConverter.class)
  public SortedSet<Integer> set;
}