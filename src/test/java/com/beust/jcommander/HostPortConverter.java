package com.beust.jcommander;

public class HostPortConverter implements IStringConverter<HostPort> {

  @Override
  public HostPort convert(String value) {
    HostPort result = new HostPort();
    String[] s = value.split(":");
    result.host = s[0];
    result.port = Integer.parseInt(s[1]);

    return result;
  }
}