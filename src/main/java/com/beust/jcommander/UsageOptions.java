package com.beust.jcommander;

public enum UsageOptions {
  /**
   * Display non-command parameters.
   */
  DISPLAY_PARAMETERS, 
  
  /**
   * Display "syntax" line.
   */
  DISPLAY_SYNTAX_LINE, 
  
  /**
   * Display commands.
   */
  DISPLAY_COMMANDS,
  
  /**
   * Displays options for each command, if commands are present and {@link #DISPLAY_COMMANDS}
   * is also passed as an option.
   */
  DISPLAY_OPTIONS_FOR_EACH_COMMAND;
}
