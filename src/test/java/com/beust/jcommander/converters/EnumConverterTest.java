/**
 * Copyright (C) 2024 the original author or authors.
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

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class EnumConverterTest {
    private static final EnumConverter<Season> SEASON_ENUM_CONVERTER = new EnumConverter<>("", Season.class);
    
    private static final EnumConverter<Resolution> RESOLUTION_ENUM_CONVERTER = new EnumConverter<>("", Resolution.class);
    
    @Test
    public void testMatch() {
        Assert.assertEquals(Season.AUTUMN, SEASON_ENUM_CONVERTER.convert("AUTUMN"));
    }
    
    @Test
    public void testMatchLowerCase() {
        Assert.assertEquals(Season.AUTUMN, SEASON_ENUM_CONVERTER.convert("autumn"));
    }
    
    @Test
    public void testMatchWithToString() {
        Assert.assertEquals(Resolution.R_4K, RESOLUTION_ENUM_CONVERTER.convert("4k"));
        Assert.assertEquals(Resolution.R_1080P, RESOLUTION_ENUM_CONVERTER.convert("1080P"));
    }
    
    @Test
    public void testNoMatch() {
        Assert.assertThrows(() -> SEASON_ENUM_CONVERTER.convert("XXX"));
        Assert.assertThrows(() -> RESOLUTION_ENUM_CONVERTER.convert("XXX"));
    }
    
    private enum Season {
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER
    }
    
    private enum Resolution {
        R_4K, // can not start with a number
        R_1080P,
        R_480P;

        @Override
        public String toString() {
            return name().substring(2); // removes the prefix for user-friendly reading
        }
    }
}
