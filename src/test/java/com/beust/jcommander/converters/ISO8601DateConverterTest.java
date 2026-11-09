package com.beust.jcommander.converters;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class ISO8601DateConverterTest {

  public static class DateArgs {
    @Parameter(names = "--date")
    public Date date;
  }

  @Test
  public void convertsDate() throws Exception {
    assertEquals(new ISO8601DateConverter("--date").convert("2011-10-26"),
        new SimpleDateFormat("yyyy-MM-dd").parse("2011-10-26"));
  }

  @Test
  public void retainsParseErrorMessage() {
    try {
      new ISO8601DateConverter("--date").convert("not-a-date");
      fail("Expected a ParameterException");
    } catch (ParameterException ex) {
      assertEquals(ex.getMessage(),
          "\"--date\": couldn't convert \"not-a-date\" to an ISO-8601 formatted date (yyyy-MM-dd)");
    }
  }

  @Test(timeOut = 30000)
  public void independentConvertersCanParseConcurrently() throws Exception {
    checkConcurrentDates(false, false);
  }

  @Test(timeOut = 30000)
  public void sharedConverterCanParseConcurrently() throws Exception {
    checkConcurrentDates(true, false);
  }

  @Test(timeOut = 30000)
  public void independentCommandersCanParseDatesConcurrently() throws Exception {
    checkConcurrentDates(false, true);
  }

  private void checkConcurrentDates(boolean shared, boolean throughCommander) throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(8);
    CountDownLatch start = new CountDownLatch(1);
    List<Future<?>> results = new ArrayList<>();
    ISO8601DateConverter sharedConverter = new ISO8601DateConverter("--date");
    try {
      for (int i = 0; i < 8; i++) {
        String value = LocalDate.of(2001 + i, 2 + i, 10 + i).toString();
        Date expected = new SimpleDateFormat("yyyy-MM-dd").parse(value);
        results.add(executor.submit(() -> {
          ISO8601DateConverter converter = shared ? sharedConverter : new ISO8601DateConverter("--date");
          start.await();
          for (int j = 0; j < 500; j++) {
            Date actual;
            if (throughCommander) {
              DateArgs args = new DateArgs();
              JCommander.newBuilder().addObject(args).build().parse("--date", value);
              actual = args.date;
            } else {
              actual = converter.convert(value);
            }
            assertEquals(actual, expected, "Incorrect date for " + value);
          }
          return null;
        }));
      }
      start.countDown();
      for (Future<?> result : results) {
        result.get(20, TimeUnit.SECONDS);
      }
    } finally {
      start.countDown();
      executor.shutdownNow();
      assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Date parser workers did not stop");
    }
  }
}
