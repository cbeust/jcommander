/**
 * Copyright (C) 2019 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beust.jcommander.defaultprovider;

import com.beust.jcommander.IDefaultProvider;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * A default provider that reads its default values from an environment
 * variable.
 *
 * A prefix pattern can be provided to indicate how options are identified.
 * The default pattern {@code -/} mandates that options MUST start with either a dash or a slash.
 * Options can have values separated by whitespace.
 * Values can contain whitespace as long as they are single-quoted or double-quoted.
 * Otherwhise whitespace identifies the end of a value.
 *
 * @author Markus KARG (markus@headcrashing.eu)
 */
public final class EnvironmentVariableDefaultProvider implements IDefaultProvider {

    private static final String DEFAULT_VARIABLE_NAME = "JCOMMANDER_OPTS";

    private static final String DEFAULT_PREFIXES_PATTERN = "-/";

    /** Value returned when an option is present but has no associated value (i.e. it's a boolean flag). */
    private static final String FLAG_PRESENT_VALUE = "true";

    private final String environmentVariableValue;

    private final String optionPrefixesPattern;

    /**
     * Creates a default provider reading the environment variable {@code JCOMMANDER_OPTS} using the prefixes pattern {@code -/}.
     */
    public EnvironmentVariableDefaultProvider() {
        this(DEFAULT_VARIABLE_NAME, DEFAULT_PREFIXES_PATTERN);
    }

    /**
     * Creates a default provider reading the specified environment variable using the specified prefixes pattern.
     *
     * @param environmentVariableName
     *            The name of the environment variable to read (e. g. {@code "JCOMMANDER_OPTS"}). Must not be {@code null}.
     * @param optionPrefixes
     *            A set of characters used to indicate the start of an option (e. g. {@code "-/"} if option names may start with either dash or slash). Must not be {@code null}.
     */
    public EnvironmentVariableDefaultProvider(final String environmentVariableName, final String optionPrefixes) {
        this(requireNonNull(environmentVariableName), requireNonNull(optionPrefixes), System::getenv);
    }

    /**
     * For Unit Tests Only: Allows to mock the resolver, as Java cannot set environment variables.
     *
     * @param environmentVariableName
     *            The name of the environment variable to read. May be {@code null} if the passed resolver doesn't use it (e. g. Unit Test).
     * @param optionPrefixes
     *            A set of characters used to indicate the start of an option (e. g. {@code "-/"} if option names may start with either dash or slash). Must not be {@code null}.
     * @param resolver
     *            Reads the value from the environment variable (e. g. {@code System::getenv}). Must not be {@code null}.
     */
    EnvironmentVariableDefaultProvider(final String environmentVariableName, final String optionPrefixes, final Function<String, String> resolver) {
        this.environmentVariableValue = resolver.apply(environmentVariableName);
        this.optionPrefixesPattern = requireNonNull(optionPrefixes);
    }

    @Override
    public final String getDefaultValueFor(final String optionName) {
        if (this.environmentVariableValue == null) {
            return null;
        }

        final Matcher matcher = buildMatcherFor(optionName);
        if (!matcher.matches()) {
            return null;
        }

        final String rawValue = matcher.group(2);
        if (rawValue == null) {
            return FLAG_PRESENT_VALUE;
        }

        return stripSurroundingQuote(rawValue);
    }

    /**
     * Searches the environment variable string for the given option name and
     * returns whatever raw value (if any) follows it.
     *
     * @return the raw matched value group, or {@code null} if the option wasn't found.
     */


    /**
     * Builds a matcher that looks for {@code optionName} inside the environment
     * variable value, capturing an optional value that follows it. The value may be:
     * single-quoted, double-quoted, or a plain run of non-whitespace,
     * non-prefix characters.
     */
    private Matcher buildMatcherFor(final String optionName) {
        final String pattern = "(?:(?:.*\\s+)|(?:^))("
                + Pattern.quote(optionName)
                + ")\\s*((?:'[^']*(?='))|(?:\"[^\"]*(?=\"))|(?:[^"
                + this.optionPrefixesPattern
                + "\\s]+))?.*";
        return Pattern.compile(pattern).matcher(this.environmentVariableValue);
    }

    private boolean isFlagWithoutValue(final String rawValue) {
        return rawValue == null;
    }

    /**
     * Removes a single leading quote character (single or double) from the value, if present.
     */
    private String stripSurroundingQuote(final String value) {
        final char firstCharacter = value.charAt(0);
        if (firstCharacter == '\'' || firstCharacter == '"') {
            return value.substring(1);
        }
        return value;
    }

}