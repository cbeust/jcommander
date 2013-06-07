package com.mdrob.jcommander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;


public class QuotedMainTest {
  public static class Options {
    @Parameter
    List<String> args = new ArrayList<String>();
    
    @Parameter(names={"-f", "--foo"})
    String other;
  }
  
  @Test
  public void testMain() {    
    String quoted = "\"quoted\"";
    Options options = new Options();
    new JCommander(options).parse(quoted);
    Assert.assertEquals(options.args, Collections.singletonList(quoted));
  }
}
