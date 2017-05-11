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

import com.beust.jcommander.IStringConverter;

/**
 * Converts a String to a {@link ByteOrder}.
 * 
 * @author Gary Gregory
 */
public final class ByteOrderConverter implements IStringConverter<ByteOrder> {

    @Override
    public ByteOrder convert(final String value) {
        if (value.equalsIgnoreCase(ByteOrder.BIG_ENDIAN.toString())) {
            return ByteOrder.BIG_ENDIAN;
        }
        if (value.equalsIgnoreCase(ByteOrder.LITTLE_ENDIAN.toString())) {
            return ByteOrder.LITTLE_ENDIAN;
        }
        throw new IllegalArgumentException(value);
    }
}
