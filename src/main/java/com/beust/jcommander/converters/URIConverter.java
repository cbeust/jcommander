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

import com.beust.jcommander.ParameterException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Convert a string into a URI.
 * 
 * @author samvv
 */
public class URIConverter extends BaseConverter<URI> {
  
  public URIConverter(String optionName) {
    super(optionName);
  }
  
  public URI convert(String value) {
    try {
      return new URI(value);
    } catch (URISyntaxException e) {
      throw new ParameterException(getErrorString(value, "a RFC 2396 and RFC 2732 compliant URI"));
    }
  }
  
}
