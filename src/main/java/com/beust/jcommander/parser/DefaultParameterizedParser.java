/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beust.jcommander.parser;

import com.beust.jcommander.IParameterizedParser;
import com.beust.jcommander.Parameterized;
import java.util.List;

/**
 *
 * @author Tim Gallagher
 */
public class DefaultParameterizedParser implements IParameterizedParser {

  @Override
  public List<Parameterized> parseArg(Object arg) {
    return Parameterized.parseArg(arg);
  }
  
}
