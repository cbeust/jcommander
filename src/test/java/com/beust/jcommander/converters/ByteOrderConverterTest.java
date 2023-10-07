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

import java.nio.ByteOrder;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link ByteOrderConverter}.
 * 
 * @author Gary Gregory
 */
@Test
public class ByteOrderConverterTest {

	private static final ByteOrderConverter CONVERTER = new ByteOrderConverter();

	@Test
	public void testBigEndian() {
		Assert.assertEquals(CONVERTER.convert("BIG_ENDIAN"), ByteOrder.BIG_ENDIAN);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGargabeInput() {
		CONVERTER.convert("!@#$%");
	}

	@Test
	public void testLittleEndian() {
		Assert.assertEquals(CONVERTER.convert("Little_Endian"), ByteOrder.LITTLE_ENDIAN);
	}

}
