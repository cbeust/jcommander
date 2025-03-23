package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.testng.Assert.assertEquals;

public class LocalDateTimeConverterTest {

  private final LocalDateTimeConverter converter = new LocalDateTimeConverter("-p");

  @Test(dataProvider = "supported")
  public void supportedFormats_ShouldConvert(String value) {
    LocalDateTime actual = converter.convert(value);
    LocalDateTime expected = LocalDateTime.of(2023, Month.MAY, 11, 9, 15, 19);
    assertEquals(actual, expected, "Incorrectly parsed local date time");
  }

  @DataProvider(name = "supported")
  public static Object[][] supported() {
    return new Object[][]{
            {"2023-05-11T09:15:19"}
    };
  }

  @Test(dataProvider = "unsupported", expectedExceptions = ParameterException.class)
  public void unsupportedFormats_ShouldThrowException(String value) {
    converter.convert(value);
  }

  @DataProvider(name = "unsupported")
  public static Object[][] unsupported() {
    return new Object[][]{
            {"2023/05/11T09:15:19"},
            {"qwe"}
    };
  }
}
