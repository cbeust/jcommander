package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalTime;

import static org.testng.Assert.assertEquals;

public class LocalTimeConverterTest {

  private final LocalTimeConverter converter = new LocalTimeConverter("-p");

  @Test(dataProvider = "supported")
  public void supportedFormats_ShouldConvert(String value) {
    LocalTime actual = converter.convert(value);
    LocalTime expected = LocalTime.of(10, 11, 0, 0);
    assertEquals(actual, expected, "Incorrectly parsed local time");
  }

  @DataProvider(name = "supported")
  public static Object[][] supported() {
    return new Object[][]{
            {"10:11:00.000"},
            {"10:11"}
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
            {"qwe"}
    };
  }
}
