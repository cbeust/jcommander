/**
 * Copyright (C) 2019 the original author or authors.
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

package com.beust.jcommander.defaultprovider;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.function.Function;

import org.testng.annotations.Test;

import com.beust.jcommander.IDefaultProvider;

public final class EnvironmentVariableDefaultProviderTest {

	@Test
	public final void shouldParseEnvironmentVariable() {
        // given
		final Function<String, String> variableResolver = name -> "--some-option --simple-value ABC --quoted-value 'A BC' --double-quoted-value \"AB C\"";
		final IDefaultProvider defaultProvider = new EnvironmentVariableDefaultProvider(null, "-", variableResolver);

		// when
		final String nonExistentValue = defaultProvider.getDefaultValueFor("--non-existent-option");
		final String someOption = defaultProvider.getDefaultValueFor("--some-option");
		final String simpleValue = defaultProvider.getDefaultValueFor("--simple-value");
		final String quotedValue = defaultProvider.getDefaultValueFor("--quoted-value");
		final String doubleQuotedValue = defaultProvider.getDefaultValueFor("--double-quoted-value");

		// then
		assertNull(nonExistentValue);
		assertEquals(someOption, "true");
		assertEquals(simpleValue, "ABC");
		assertEquals(quotedValue, "A BC");
		assertEquals(doubleQuotedValue, "AB C");
	}

}
