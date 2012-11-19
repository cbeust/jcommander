package com.beust.jcommander;

import org.testng.Assert;

public class Assertions {
	private Assertions() {		
	}
	
	public static void assertContains(String value, String expected) {
		if (!value.contains(expected)) {
			Assert.fail("Text >" + value +"< was expected to contain >" + expected +"<");
		}
	}
}
