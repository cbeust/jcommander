package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Instant;

import static org.testng.Assert.assertEquals;

public class InstantConverterTest {

  private final InstantConverter converter = new InstantConverter("--op");

  @Test(dataProvider = "data")
  public void longValue_ShouldConvert(String msValue, String expectedIsoValue) {
    Instant actual = converter.convert(msValue);
    Instant expected = Instant.parse(expectedIsoValue);
    assertEquals(actual, expected, "Incorrectly parsed instant from millis");
  }

  @DataProvider(name = "data")
  public static Object[][] data() {
    return new Object[][]{
            {"111", "1970-01-01T00:00:00.111Z"},
            {"1234567890", "1970-01-15T06:56:07.890Z"},
            {"-11", "1969-12-31T23:59:59.989Z"}
    };
  }

  @Test(dataProvider = "data")
  public void isoValue_ShouldConvert(String expectedMsValue, String isoValue) {
    Instant actual = converter.convert(isoValue);
    Instant expected = Instant.ofEpochMilli(Long.parseLong(expectedMsValue));
    assertEquals(actual, expected, "Incorrectly parsed instant from ISO string");
  }

  @Test(expectedExceptions = ParameterException.class)
  public void unsupportedFormatValue_ShouldThrowException() {
    converter.convert("qwerty");
  }
}
