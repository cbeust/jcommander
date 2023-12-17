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

import java.nio.charset.StandardCharsets;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link CharsetConverter}.
 * 
 * @author Gary Gregory
 */
@Test
public class CharsetConverterTest {

	private static final CharsetConverter CONVERTER = new CharsetConverter();

	@Test
	public void testUtf8() {
		test("UTF-8");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGarbageInput() {
		test("   ");
	}

	private void test(String string) {
		Assert.assertEquals(CONVERTER.convert(string), StandardCharsets.UTF_8);

	}
}
