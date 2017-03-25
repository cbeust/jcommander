package com.beust.jcommander.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.validators.PositiveInteger;

public class ArgsMultiValidate {
  public static class OddIntegerParameterValidator implements IParameterValidator {
	  @Override
	public void validate(String name, String value) throws ParameterException {
		if(Integer.parseInt(value)%2!=1) throw new ParameterException("param "+name+"="+value+" is not odd");
	}
  }

  public static class LowerThan100VvalueValidator implements IValueValidator<Integer> {
	 @Override
	public void validate(String name, Integer value) throws ParameterException {
			if(value>=100) throw new ParameterException("param "+name+"="+value+" is greater than 100");
	 }
  } 
  
  public static class GreaterTha0VvalueValidator implements IValueValidator<Integer> {
		 @Override
		public void validate(String name, Integer value) throws ParameterException {
			 System.err.println(name+"="+value+"######################");
			 if(value<=0) throw new ParameterException("param "+name+"="+value+" is lower than 1");
		 }
	  }
  
  @Parameter(names = "-age",
		  validateWith = {PositiveInteger.class,OddIntegerParameterValidator.class},
		  validateValueWith={GreaterTha0VvalueValidator.class,LowerThan100VvalueValidator.class})
  public int age=29;
}
