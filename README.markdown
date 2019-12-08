JCommander
==========

This is an annotation based parameter parsing framework for Java 8.

Here is a quick example:

```java
public class JCommanderTest {
    @Parameter
    public List<String> parameters = Lists.newArrayList();
 
    @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
    public Integer verbose = 1;
 
    @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
    public String groups;
 
    @Parameter(names = "-debug", description = "Debug mode")
    public boolean debug = false;

    @DynamicParameter(names = "-D", description = "Dynamic parameters go here")
    public Map<String, String> dynamicParams = new HashMap<String, String>();

}
```

and how you use it:

```java
JCommanderTest jct = new JCommanderTest();
String[] argv = { "-log", "2", "-groups", "unit1,unit2,unit3",
                    "-debug", "-Doption=value", "a", "b", "c" };
JCommander.newBuilder()
  .addObject(jct)
  .build()
  .parse(argv);

Assert.assertEquals(2, jct.verbose.intValue());
Assert.assertEquals("unit1,unit2,unit3", jct.groups);
Assert.assertEquals(true, jct.debug);
Assert.assertEquals("value", jct.dynamicParams.get("option"));
Assert.assertEquals(Arrays.asList("a", "b", "c"), jct.parameters);
```

The full doc is available at [https://jcommander.org](https://jcommander.org).

## Building JCommander

```
./gradlew assemble
```

