package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandNames = {"command"}, commandDescription = "text text text text text " +
        "text text text text text text text text text text text text text text text " +
        "really-really-really-long-word-or-url text text text text text text text.")
public class ArgsLongCommandDescription {
    @Parameter(names = {"-b"}, description = "boolean parameter")
    public boolean var;
}
