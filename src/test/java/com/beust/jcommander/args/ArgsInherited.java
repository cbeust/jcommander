package com.beust.jcommander.args;

import com.beust.jcommander.Parameter;

public class ArgsInherited extends ArgsDefault {

    @Parameter(names = "-child", description = "Child parameter")
    public Integer child = 1;

}
