package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class ArgsLongMainParameterDescription {

    @Parameter(description = "[text] [text] text text text text text text text text " +
            "text text text text text text text text " +
            "really-really-really-long-word-or-url text text text text text text text.")
    public List<String> main = new ArrayList<>();

    @Parameter(names = {"-b"}, description = "boolean parameter")
    public boolean var;
}
