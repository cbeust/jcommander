package com.beust.jcommander.dynamic;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

import java.util.HashMap;
import java.util.Map;

public class ShadowingParameter {

    @Parameter(
            names = "-abc"
    )
    String abc;
    @DynamicParameter(
            names = "-ab"
    )
    Map<String,String> ab = new HashMap<>();

}
