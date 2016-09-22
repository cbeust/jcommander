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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class InetAddressConverterTest {

	private static final InetAddressConverter INET_ADDRESS_CONVERTER = new InetAddressConverter();
	private static final InetAddress LOOPBACK_ADDRESS = InetAddress.getLoopbackAddress();

	@Test
	public void testLocalhost() throws UnknownHostException {
		test("localhost");
	}

	@Test
	public void testLocalhostAddress() throws UnknownHostException {
		test("127.0.0.1");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGargabeInput() throws UnknownHostException {
		test("!@#$%");
	}

	@Test
	public void testEmptyInput() throws UnknownHostException {
		test("");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testBlankInput() throws UnknownHostException {
		test("   ");
	}

	private void test(String string) throws UnknownHostException {
		Assert.assertEquals(INET_ADDRESS_CONVERTER.convert(string), LOOPBACK_ADDRESS);

	}
}
