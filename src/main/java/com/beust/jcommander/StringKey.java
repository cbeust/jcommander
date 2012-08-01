package com.beust.jcommander;

import com.beust.jcommander.FuzzyMap.IKey;

public class StringKey implements IKey {

  private String m_name;

  public StringKey(String name) {
    m_name = name;
  }

  @Override
  public String getName() {
    return m_name;
  }

  @Override
  public String toString() {
    return m_name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StringKey other = (StringKey) obj;
    if (m_name == null) {
      if (other.m_name != null)
        return false;
    } else if (!m_name.equals(other.m_name))
      return false;
    return true;
  }

}
