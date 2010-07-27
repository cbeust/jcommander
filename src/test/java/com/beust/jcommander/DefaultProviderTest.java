package com.beust.jcommander;

import com.beust.jcommander.defaultprovider.PropertyFileDefaultProvider;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DefaultProviderTest {
  private static final IDefaultProvider DEFAULT_PROVIDER = new IDefaultProvider() {

    @Override
    public String getDefaultValueFor(String optionName) {
      return "-debug".equals(optionName) ? "false" : "42";
    }
    
  };

  private ArgsDefault defaultProvider(IDefaultProvider provider, String... args) {
    ArgsDefault a = new ArgsDefault();
    JCommander jc = new JCommander(a);
    jc.setDefaultProvider(provider);

    jc.parse(args);
    return a;
  }

  @Test
  public void defaultProvider1() {
    ArgsDefault a = defaultProvider(DEFAULT_PROVIDER, "f");

    Assert.assertEquals(a.groups, "42");
    Assert.assertEquals(a.level, 42);
    Assert.assertEquals(a.log.intValue(), 42);
  }

  @Test
  public void defaultProvider2() {
    ArgsDefault a = defaultProvider(DEFAULT_PROVIDER, "-groups", "foo", "f");

    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 42);
    Assert.assertEquals(a.log.intValue(), 42);
  }

  @Test
  public void defaultProvider3() {
    ArgsDefault a = defaultProvider(DEFAULT_PROVIDER, "-groups", "foo", "-level", "13", "f");

    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 13);
    Assert.assertEquals(a.log.intValue(), 42);
  }

  @Test
  public void defaultProvider4() {
    ArgsDefault a = defaultProvider(DEFAULT_PROVIDER,
        "-log", "19", "-groups", "foo", "-level", "13", "f");

    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 13);
    Assert.assertEquals(a.log.intValue(), 19);
  }

  @Test
  public void propertyFileDefaultProvider1() {
    ArgsDefault a = defaultProvider(new PropertyFileDefaultProvider(), "f");

    Assert.assertEquals(a.groups, "unit");
    Assert.assertEquals(a.level, 17);
    Assert.assertEquals(a.log.intValue(), 18);
  }

  @Test
  public void propertyFileDefaultProvider2() {
    ArgsDefault a = defaultProvider(new PropertyFileDefaultProvider(), "-groups", "foo", "f");
    
    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 17);
    Assert.assertEquals(a.log.intValue(), 18);
  }

  @Test
  public void propertyFileDefaultProvider3() {
    ArgsDefault a = defaultProvider(new PropertyFileDefaultProvider(),
        "-groups", "foo", "-level", "13", "f");

    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 13);
    Assert.assertEquals(a.log.intValue(), 18);
  }

  @Test
  public void propertyFileDefaultProvider4() {
    ArgsDefault a = defaultProvider(new PropertyFileDefaultProvider(),
        "-log", "19", "-groups", "foo", "-level", "13", "f");

    Assert.assertEquals(a.groups, "foo");
    Assert.assertEquals(a.level, 13);
    Assert.assertEquals(a.log.intValue(), 19);
  }

}
