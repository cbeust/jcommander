/**
 * Copyright (C) 2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beust.jcommander.converters;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;

/**
 * {@link URLConverter} unit tests.
 * 
 * @author Alin Dreghiciu <adreghiciu@gmail.com>
 */
@Test
public class URLConverterTest {

	/**
	 * Convert to an URL should not fail.
	 */
	public void convertToURL() {
		Args args = new Args();
		String[] argv = { "-url", "http://jcomander.com" };
		new JCommander(args, argv);

	    Assert.assertEquals(args.url.toExternalForm(), "http://jcomander.com");
	}

	/**
	 * Having a malformed url should throw an exception.
	 */
	@Test(expectedExceptions = ParameterException.class)
	public void convertMalformedURL() {
		Args args = new Args();
		String[] argv = { "-url", "some malformed url" };
		new JCommander(args, argv);
	}

	private static class Args {
		@Parameter(names = "-url", converter = URLConverter.class)
		public URL url;
	}

}
