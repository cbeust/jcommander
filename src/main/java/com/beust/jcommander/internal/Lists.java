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

package com.beust.jcommander.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Lists {

    public static <K> List<K> newArrayList() {
        return new ArrayList<K>();
    }

    public static <K> List<K> newArrayList(Collection<K> c) {
        return new ArrayList<K>(c);
    }

    public static <K> List<K> newArrayList(K... c) {
      return new ArrayList<K>(Arrays.asList(c));
    }

    public static <K> List<K> newArrayList(int size) {
        return new ArrayList<K>(size);
    }

    public static <K> LinkedList<K> newLinkedList() {
        return new LinkedList<K>();
    }

    public static <K> LinkedList<K> newLinkedList(Collection<K> c) {
        return new LinkedList<K>(c);
    }


}
