package com.beust.jcommander;

import java.io.File;
import java.util.ResourceBundle;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * The following test shows how to implements a custom jcommander
 * that will handle a special type of object.
 * @author Pierre Lindenbaum
 *
 */
@Test
public class SubClassingJCommanderTest {
	
	
	/** Defines method to set the value of an object as a string*/
	public static interface SetValueAsString {
		public void setValueAsString(final String s) throws ParameterException;
	}
	
	/** the default TMP_DIR */
	private static final File DEFAULT_TMP_DIR = new File(System.getProperty("java.io.tmpdir"));
	
	/** an example implementation of SetValueAsString */
	public static class TmpDirectory
		implements SetValueAsString
		{
		private File tmpDir= DEFAULT_TMP_DIR;
		private boolean value_changed=false;
		@Override
		public void setValueAsString(String s) throws ParameterException {
			this.tmpDir=new File(s);
			this.value_changed = true;
			}
		public File get() {
			return this.tmpDir;
			}
		}
	
	/** 
	 * a custom implementation of ParameterDescription 
	 * if the field implements 'SetValueAsString', this
	 * class will use the `setValueAsString` instead of the
	 * standard way to set the value
	 */
	private static class CustomParameterDescription
		extends ParameterDescription
		{
		public CustomParameterDescription(Object object, Parameter annotation, Parameterized parameterized,
			ResourceBundle bundle, JCommander jc) {
			super(object, annotation, parameterized, bundle, jc);
		}
		@Override
		Object addValue(String name, String value, boolean isDefault, boolean validate, int currentIndex) {
			if(SetValueAsString.class.isAssignableFrom(getParameterized().getType())) {	
				SetValueAsString.class.cast(this.getParameterized().get(getObject())).setValueAsString(value);
				return getObject();
				}
			else {
				return super.addValue(name,value,isDefault,validate,currentIndex);
				}
			}
		
		}
	/** an implementation of jcommander using the CustomParameterDescription above */
	public static class MyJcommander extends JCommander
		{
		@Override
		protected ParameterDescription createParameterDescription(
				Object object, Parameter annotation,
			Parameterized parameterized, ResourceBundle bundle) {
			return new CustomParameterDescription(object, annotation, parameterized, bundle,this);
			}
		}
	
	public class Args1 {
    @Parameter(names = "-T", description = "Temporary Directory")
    public TmpDirectory tmpDir = new TmpDirectory();

    @Parameter(names = "-N", description = "A number")
    public int number=2;
	}
	
	@Test
	public void testSubJCOmmander() {
		Args1 args=new Args1();
		MyJcommander jc = new MyJcommander();
		jc.addObject(args);
		jc.parse("-T","tmp","-N","3");
		Assert.assertNotNull(args);
		Assert.assertNotNull(args.tmpDir);
		Assert.assertTrue(args.tmpDir.value_changed);
		Assert.assertEquals(args.number,3);
		
		args=new Args1();
		jc = new MyJcommander();
		jc.addObject(args);
		jc.parse("-N","3");
		Assert.assertNotNull(args);
		Assert.assertNotNull(args.tmpDir);
		Assert.assertFalse(args.tmpDir.value_changed);
		Assert.assertEquals(args.tmpDir.get(),DEFAULT_TMP_DIR);
		Assert.assertEquals(args.number,3);
	}
	

}
