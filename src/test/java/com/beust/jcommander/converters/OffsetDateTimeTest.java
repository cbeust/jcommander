package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.testng.Assert.assertEquals;

public class OffsetDateTimeTest {

  private final OffsetDateTimeConverter converter = new OffsetDateTimeConverter("-p");

  @Test(dataProvider = "supported")
  public void supportedFormats_ShouldConvert(String value) {
    OffsetDateTime actual = converter.convert(value);
    OffsetDateTime expected = OffsetDateTime.of(LocalDateTime.of(2023, Month.MAY, 11, 9, 15, 19), ZoneOffset.UTC);
    assertEquals(actual, expected, "Incorrectly parsed offset date time");
  }

  @DataProvider(name = "supported")
  public static Object[][] supported() {
    return new Object[][]{
            {"2023-05-11T09:15:19+00:00"}
    };
  }

  @Test(dataProvider = "unsupported", expectedExceptions = ParameterException.class)
  public void unsupportedFormats_ShouldThrowException(String value) {
    converter.convert(value);
  }

  @DataProvider(name = "unsupported")
  public static Object[][] unsupported() {
    return new Object[][]{
            {"2023-05-11T09:15:19"},
            {"qwe"}
    };
  }
}
