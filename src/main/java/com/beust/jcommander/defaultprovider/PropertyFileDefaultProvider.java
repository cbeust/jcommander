/**
 * Copyright (C) 2010 the original author or authors.
 * ...license header unchanged...
 */

package com.beust.jcommander.defaultprovider;

import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.ParameterException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Function;

/**
 * A default provider that reads its default values from a property file.
 *
 * @author cbeust
 */
public class PropertyFileDefaultProvider implements IDefaultProvider {

  public static final String DEFAULT_FILE_NAME = "jcommander.properties";

  private final Properties properties = new Properties();
  private final Function<String, String> optionNameTransformer;

  // -------------------------------------------------------------------------
  // Constructors — all chain down to the most specific variant
  // -------------------------------------------------------------------------

  public PropertyFileDefaultProvider() {
    this(DEFAULT_FILE_NAME);
  }

  public PropertyFileDefaultProvider(final String fileName) {
    this(fileName, PropertyFileDefaultProvider::stripLeadingNonAlphanumeric);
  }

  public PropertyFileDefaultProvider(final String fileName, final Function<String, String> optionNameTransformer) {
    loadFromFileName(fileName);
    this.optionNameTransformer = optionNameTransformer;
  }

  public PropertyFileDefaultProvider(final Path path) {
    this(path, PropertyFileDefaultProvider::stripLeadingNonAlphanumeric);
  }

  public PropertyFileDefaultProvider(final Path path, final Function<String, String> optionNameTransformer) {
    loadFromPath(path);
    this.optionNameTransformer = optionNameTransformer;
  }

  // -------------------------------------------------------------------------
  // IDefaultProvider
  // -------------------------------------------------------------------------

  @Override
  public String getDefaultValueFor(final String optionName) {
    return properties.getProperty(optionName.transform(optionNameTransformer));
  }

  // -------------------------------------------------------------------------
  // Private helpers
  // -------------------------------------------------------------------------

  /**
   * Strips any leading non-alphanumeric characters from an option name.
   * For example, {@code "--verbose"} becomes {@code "verbose"}, and
   * {@code "-count"} becomes {@code "count"}.
   */
  private static String stripLeadingNonAlphanumeric(final String optionName) {
    int index = 0;
    while (index < optionName.length() && !Character.isLetterOrDigit(optionName.charAt(index))) {
      index++;
    }
    return optionName.substring(index);
  }

  private void loadFromFileName(final String fileName) {
    try {
      final URL url = ClassLoader.getSystemResource(fileName);
      if (url == null) {
        throw new ParameterException("Could not find property file: " + fileName + " on the class path");
      }
      properties.load(url.openStream());
    } catch (final IOException e) {
      throw new ParameterException("Could not open property file: " + fileName);
    }
  }

  private void loadFromPath(final Path path) {
    try (final var inputStream = Files.newInputStream(path)) {
      properties.load(inputStream);
    } catch (final IOException e) {
      throw new ParameterException("Could not load properties from path: " + path);
    }
  }

}