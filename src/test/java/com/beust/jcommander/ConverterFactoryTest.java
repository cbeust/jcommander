package com.beust.jcommander;

import com.beust.jcommander.args.ArgsConverterFactory;
import com.beust.jcommander.args.ArgsMainParameter;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test the converter factory feature.
 * 
 * @author cbeust
 */
public class ConverterFactoryTest {
  private static final Map<Class, Class<? extends IStringConverter<?>>> MAP = new HashMap() {{
    put(HostPort.class, HostPortConverter.class);
  }};

  private static final IStringConverterFactory CONVERTER_FACTORY = new IStringConverterFactory() {

    @Override
    public Class<? extends IStringConverter<?>> getConverter(Class forType) {
      return MAP.get(forType);
    }
    
  };

  @Test
  public void converterFactory() {
    ArgsConverterFactory a = new ArgsConverterFactory();
    JCommander jc = new JCommander(a);
    jc.addConverterFactory(CONVERTER_FACTORY);
    jc.parse("-hostport", "example.com:8080");

    Assert.assertEquals(a.hostPort.host, "example.com");
    Assert.assertEquals(a.hostPort.port.intValue(), 8080);
  }

  @Test
  public void mainWithHostPortParameters() {
    ArgsMainParameter a = new ArgsMainParameter();
    JCommander jc = new JCommander(a);
    jc.addConverterFactory(CONVERTER_FACTORY);
    jc.parse("a.com:10", "b.com:20");
    Assert.assertEquals(a.parameters.get(0).host, "a.com");
    Assert.assertEquals(a.parameters.get(0).port.intValue(), 10);
    Assert.assertEquals(a.parameters.get(1).host, "b.com");
    Assert.assertEquals(a.parameters.get(1).port.intValue(), 20);
  }

}

class HostPortConverter implements IStringConverter<HostPort> {

  @Override
  public HostPort convert(String value) {
    HostPort result = new HostPort();
    String[] s = value.split(":");
    result.host = s[0];
    result.port = Integer.parseInt(s[1]);

    return result;
  }
}