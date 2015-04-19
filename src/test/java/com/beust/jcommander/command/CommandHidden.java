package com.beust.jcommander.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(commandNames = "add", commandDescription = "Hidden command to add file contents to the index", hidden = true)
public class CommandHidden {

    @Parameter(description = "Patterns of files to be added")
    public List<String> patterns;

    @Parameter(names = "-i")
    public Boolean interactive = false;

}
