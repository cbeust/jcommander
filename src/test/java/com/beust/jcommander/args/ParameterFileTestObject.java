package com.beust.jcommander.args;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterFile;

public class ParameterFileTestObject {

	@Parameter(names="--intValue")
	public int intValue;
	
	@Parameter(names="--strValue")
	public String strValue;
	
	@Parameter(names="--simpleBooleanTrue", required=true)
	public boolean simpleBooleanTrue;
	
	@Parameter(names="--simpleBooleanFalse")
	public boolean simpleBooleanFalse;
	
	@ParameterFile
	@Parameter(names="--file")
	public File optionsFile;
	
	@Parameter(names="--stringTwoArity", arity=2)
	public List<String> arityTwo;
	
	@Parameter(names="--stringVarArity", variableArity = true)
	public List<String> varArity;
	
	
}
