JCommander
==========

This is an annotation based parameter parsing framework for Java.

Here is a quick example:

    public class JCommanderTest {
      @Parameter
      public List<String> parameters = Lists.newArrayList();
  
      @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
      public Integer verbose = 1;
  
      @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
      public String groups;
  
      @Parameter(names = "-debug", description = "Debug mode")
      public boolean debug = false;
    }

and how you use it:

    JCommanderTest jct = new JCommanderTest();
    String[] argv = { "-log", "2", "-groups", "unit", "a", "b", "c" };
    new JCommander(jct, argv);

    Assert.assertEquals(jct.verbose.intValue(), 2);

The full doc is available at http://beust.com/jcommander
