package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

import javax.annotation.PostConstruct;
import java.util.List;

public class ArgsPostConstruct
{
  @Parameter(names = "-param1")
  public String param1;

  @Parameter(names = "-param2")
  public String param2;

  public String concatParam;

  @Parameter(arity = 2)
  public List<String> main;
  public String host;
  public int port;

  @PostConstruct
  private void init() {
    concatParam = param1 + param2;
    host = main.get(0);
    port = Integer.parseInt(main.get(1));
  }
}
