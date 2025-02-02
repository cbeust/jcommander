package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import static org.testng.Assert.assertEquals;

public class OffsetTimeConverterTest {

  private final OffsetTimeConverter converter = new OffsetTimeConverter("-p");

  @Test(dataProvider = "supported")
  public void supportedFormats_ShouldConvert(String value) {
    OffsetTime actual = converter.convert(value);
    OffsetTime expected = OffsetTime.of(LocalTime.of(10, 11, 0, 0), ZoneOffset.UTC);
    assertEquals(actual, expected, "Incorrectly parsed offset time");
  }

  @DataProvider(name = "supported")
  public static Object[][] supported() {
    return new Object[][]{
            {"10:11:00.000+00:00"},
            {"10:11+00:00"}
    };
  }

  @Test(dataProvider = "unsupported", expectedExceptions = ParameterException.class)
  public void unsupportedFormats_ShouldThrowException(String value) {
    converter.convert(value);
  }

  @DataProvider(name = "unsupported")
  public static Object[][] unsupported() {
    return new Object[][]{
            {"10-11"},
            {"10:11"},
            {"qwe"}
    };
  }
}
