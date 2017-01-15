package com.beust.jcommander;

/**
 * A factory to create {@link IStringConverter} instances.
 *
 * This interface lets you specify your converters in one place instead of having them repeated all over your argument classes.
 *
 * @author simon04
 * @see IStringConverterFactory
 */
public interface IStringConverterInstanceFactory {
    /**
     * Obtain a converter instance for parsing {@code parameter} as type {@code forType}
     * @param parameter the parameter to parse
     * @param forType the type class
     * @param optionName the name of the option used on the command line
     * @return a converter instance
     */
    IStringConverter<?> getConverterInstance(Parameter parameter, Class<?> forType, String optionName);
}
