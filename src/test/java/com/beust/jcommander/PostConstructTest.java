package com.beust.jcommander;

import com.beust.jcommander.args.ArgsPostConstruct;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PostConstructTest {

  @Test
  public void testPostConstruct() {
    String input = "-param1 one -param2 two localhost 1234";
    String[] split = input.split("\\s+");

    ArgsPostConstruct config = new ArgsPostConstruct();
    JCommander com = new JCommander(config);

    com.parse(split);

    Assert.assertEquals(config.param1, "one");
    Assert.assertEquals(config.param2, "two");
    Assert.assertEquals(config.concatParam, "onetwo");
    Assert.assertEquals(config.host, "localhost");
    Assert.assertEquals(config.port, 1234);
  }
}
