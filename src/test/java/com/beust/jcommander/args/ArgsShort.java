package com.beust.jcommander.args;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Test combined short arguments.
 *
 * @author rddunphy
 */
public class ArgsShort {
    @Parameter(names = {"-a", "--longname"}, description = "Boolean A")
    public boolean a;

    @Parameter(names = "-b", description = "Boolean B")
    public boolean b;

    @Parameter(names = "-c", description = "Boolean C")
    public boolean c;

    @Parameter(names = "-s", description = "String")
    public String s;

    @DynamicParameter(names = "-D", description = "Dynamic parameters")
    public Map<String, String> params = new HashMap<>();

}
