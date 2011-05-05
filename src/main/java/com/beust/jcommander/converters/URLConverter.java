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

import java.net.MalformedURLException;
import java.net.URL;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.BaseConverter;

/**
 * Converts a string to an URL.
 * 
 * @author Alin Dreghiciu <adreghiciu@gmail.com>
 */
public class URLConverter extends BaseConverter<URL> {

	public URLConverter(final String optionName) {
		super(optionName);
	}

	public URL convert(final String value) {
		try {
			return new URL(value);
		} catch (final MalformedURLException e) {
			throw new ParameterException(getErrorString(value,
					String.format("an url (%s)", e.getMessage())));
		}
	}

}
