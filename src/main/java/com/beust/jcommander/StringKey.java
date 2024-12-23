package com.beust.jcommander;

import com.beust.jcommander.FuzzyMap.IKey;

public record StringKey(String name) implements IKey {

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

}
