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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;

public class QNameConverterTest {

	private static final QNameConverter CONVERTER = new QNameConverter(null);
	private static final String EMPTY_STRING = "";

	@Test
	public void testEmptyString() {
		QName qname = CONVERTER.convert(EMPTY_STRING);
		Assert.assertEquals(XMLConstants.DEFAULT_NS_PREFIX, qname.getPrefix());
		Assert.assertEquals(EMPTY_STRING, qname.getNamespaceURI());
		Assert.assertEquals(EMPTY_STRING, qname.getLocalPart());
	}

	@Test
	public void testLocalPart() {
		QName qname = CONVERTER.convert("ALocalPart");
		Assert.assertEquals(XMLConstants.DEFAULT_NS_PREFIX, qname.getPrefix());
		Assert.assertEquals(EMPTY_STRING, qname.getNamespaceURI());
		Assert.assertEquals("ALocalPart", qname.getLocalPart());
	}

	@Test
	public void testNamespaceUriLocalPart() {
		QName qname = CONVERTER.convert("{namespaceUri}ALocalPart");
		Assert.assertEquals(XMLConstants.DEFAULT_NS_PREFIX, qname.getPrefix());
		Assert.assertEquals("namespaceUri", qname.getNamespaceURI());
		Assert.assertEquals("ALocalPart", qname.getLocalPart());
	}

	@Test
	public void testPrefixNamespaceUriLocalPart() {
		QName qname = CONVERTER.convert("{abc:namespaceUri}ALocalPart");
		Assert.assertEquals(XMLConstants.DEFAULT_NS_PREFIX, qname.getPrefix());
		Assert.assertEquals("abc:namespaceUri", qname.getNamespaceURI());
		Assert.assertEquals("ALocalPart", qname.getLocalPart());
	}
}
