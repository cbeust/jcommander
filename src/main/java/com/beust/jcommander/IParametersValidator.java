package com.beust.jcommander;

import java.util.Map;

public interface IParametersValidator {

    /**
     * Validate all parameters.
     *
     * @param parameters
     *            Name-value-pairs of all parameters (e.g. "-host":"localhost").
     *
     * @throws ParameterException
     *             Thrown if validation of the parameters fails.
     */
    void validate(Map<String, Object> parameters) throws ParameterException;

}
