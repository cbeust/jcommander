package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

public class JavaTimeParameters {

  @Parameter(names = "-i")
  public Instant instant;

  @Parameter(names = "-ld")
  public LocalDate localDate;

  @Parameter(names = "-ldt")
  public LocalDateTime localDateTime;

  @Parameter(names = "-lt")
  public LocalTime localTime;

  @Parameter(names = "-odt")
  public OffsetDateTime offsetDateTime;

  @Parameter(names = "-ot")
  public OffsetTime offsetTime;

  @Parameter(names = "-zdt")
  public ZonedDateTime zonedDateTime;
}
