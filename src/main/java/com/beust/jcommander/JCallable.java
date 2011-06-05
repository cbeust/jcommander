package com.beust.jcommander;

/**
 * A callable that is associated with a command and
 * gets executed when the associated command is parsed
 * from command line arguments.
 *
 * @author rodionmoiseev
 */
public interface JCallable<T> {
  /**
   * Called whenever the associated command is parsed.
   * @param parsedOpts Options object associated with
   *                    the command.
   * @throws Exception
   */
  void call(T parsedOpts) throws Exception;
}
