package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.testng.Assert.assertEquals;

public class LocalDateConverterTest {

  private final LocalDateConverter converter = new LocalDateConverter("-p");

  @Test(dataProvider = "supported")
  public void supportedFormats_ShouldConvert(String value) {
    LocalDate actual = converter.convert(value);
    LocalDate expected = LocalDate.of(2023, Month.MAY, 11);
    assertEquals(actual, expected, "Incorrectly parsed local date");
  }

  @DataProvider(name = "supported")
  public static Object[][] supported() {
    return new Object[][]{
            {"2023-05-11"},
            {"11-05-2023"}
    };
  }

  @Test(dataProvider = "unsupported", expectedExceptions = ParameterException.class)
  public void unsupportedFormats_ShouldThrowException(String value) {
    converter.convert(value);
  }

  @DataProvider(name = "unsupported")
  public static Object[][] unsupported() {
    return new Object[][]{
            {"2023:05:11"},
            {"asd"}
    };
  }
}
