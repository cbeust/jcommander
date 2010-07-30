package com.beust.jcommander.args;

import com.beust.jcommander.HostPort;
import com.beust.jcommander.HostPortConverter;
import com.beust.jcommander.Parameter;

import org.testng.collections.Lists;

import java.util.List;

/**
 * A class with main parameter that is not a List<String>
 * 
 * @author cbeust
 */
public class ArgsMainParameter2 implements IHostPorts {
  @Parameter(converter = HostPortConverter.class)
  public List<HostPort> parameters = Lists.newArrayList();

  @Override
  public List<HostPort> getHostPorts() {
    return parameters;
  }
}
