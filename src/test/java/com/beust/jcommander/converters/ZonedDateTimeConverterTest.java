package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.testng.Assert.assertEquals;

public class ZonedDateTimeConverterTest {

  private final ZonedDateTimeConverter converter = new ZonedDateTimeConverter("-p");

  @Test(dataProvider = "supported")
  public void supportedFormats_ShouldConvert(String value, String expectedZoneId) {
    ZonedDateTime actual = converter.convert(value);
    ZonedDateTime expected = ZonedDateTime.of(LocalDate.of(2023, Month.MAY, 11), LocalTime.of(10, 15, 0, 0), ZoneId.of(expectedZoneId));
    assertEquals(actual, expected, "Incorrectly parsed zoned date time");
  }

  @DataProvider(name = "supported")
  public static Object[][] supported() {
    return new Object[][]{
            {"2023-05-11T10:15:00+00:00[UTC]", "UTC"},
            {"2023-05-11T10:15:00.000+00:00[GMT]", "GMT"},
            {"2023-05-11T10:15Z", "Z"}
    };
  }

  @Test(dataProvider = "unsupported", expectedExceptions = ParameterException.class)
  public void unsupportedFormats_ShouldThrowException(String value) {
    converter.convert(value);
  }

  @DataProvider(name = "unsupported")
  public static Object[][] unsupported() {
    return new Object[][]{
            {"2023-05-11T10:15"},
            {"qwe"}
    };
  }
}
