package com.beust.jcommander;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

import com.beust.jcommander.args.ParameterFileTestObject;

public class ParameterFileTest {

	
	public String getFile(String name)
	{
		return ClassLoader.getSystemClassLoader().getResource(name).getFile();
		
	}
	@Test
	public void testFileLoad()
	{
		ParameterFileTestObject tobj = new ParameterFileTestObject();
		
		JCommander jcom = new JCommander(tobj);
		
		
		/**System.out.println(System.getProperty("java.class.path"));
		System.out.println(u);
		*/
		
		
		String file = getFile("validOptionFile.txt");
		System.out.println(file);
		String[] args = {"--file",file};
		
		jcom.parse(args);
		
		Assert.assertEquals(tobj.intValue,3);
		Assert.assertEquals(tobj.strValue,"Hello");
		Assert.assertEquals(tobj.simpleBooleanTrue, true);
		Assert.assertEquals(tobj.simpleBooleanFalse, false);
	}
	
	
	@Test(expected=ParameterException.class)
	public void missingFileError()
	{
		ParameterFileTest tobj = new ParameterFileTest();
		
		JCommander jcom = new JCommander(tobj);
		String[] args = {"--file", "/hello"};
		
		jcom.parse(args);
	}
	
	
	@Test
	public void testRequiredParameters()
	{
		ParameterFileTestObject tobj = new ParameterFileTestObject();
		
		JCommander jcom = new JCommander(tobj);
		String[] args = {"--file", getFile("validOptionFile.txt"), "--simpleBooleanTrue"};
		
		jcom.parse(args);
		
		Assert.assertEquals(tobj.intValue,3);
		Assert.assertEquals(tobj.strValue,"Hello");
		Assert.assertEquals(tobj.simpleBooleanTrue, true);
		Assert.assertEquals(tobj.simpleBooleanFalse, false);
	}
	
	@Test(expected=ParameterException.class)
	public void testRequiredParameterMissing(){
		ParameterFileTestObject tobj = new ParameterFileTestObject();
		
		JCommander jcom = new JCommander(tobj);
		String[] args = {"--file", getFile("validButMissingRequired.txt")};
		
		jcom.parse(args);
		
	}
	
	@Test
	public void testCommandLineOverride()
	{
		ParameterFileTestObject tobj = new ParameterFileTestObject();
		
		JCommander jcom = new JCommander(tobj);
		
		
		/**System.out.println(System.getProperty("java.class.path"));
		System.out.println(u);
		*/
		
		
		String file = getFile("validOptionFile.txt");
		System.out.println(file);
		String[] args = {"--file",file,"--intValue","42"};
		
		jcom.parse(args);
		
		Assert.assertEquals(tobj.intValue,42);
		Assert.assertEquals(tobj.strValue,"Hello");
		Assert.assertEquals(tobj.simpleBooleanTrue, true);
		Assert.assertEquals(tobj.simpleBooleanFalse, false);
	}
	
	
	
	@Test
	public void variableArity()
	{
		ParameterFileTestObject tobj = new ParameterFileTestObject();
		
		JCommander jcom = new JCommander(tobj);
		
		
		/**System.out.println(System.getProperty("java.class.path"));
		System.out.println(u);
		*/
		
		
		String file = getFile("validTwoArityIncluded.txt");
		System.out.println(file);
		String[] args = {"--file",file,"--intValue","42"};
		
		jcom.parse(args);
		
		Assert.assertEquals(tobj.intValue,42);
		Assert.assertEquals(tobj.strValue,"Hello");
		Assert.assertEquals(tobj.simpleBooleanTrue, true);
		Assert.assertEquals(tobj.varArity.get(0), "Hello");
		Assert.assertEquals(tobj.varArity.get(1),"There");
	}
}
