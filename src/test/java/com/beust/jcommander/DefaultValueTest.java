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

package com.beust.jcommander;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Test behaviour of default parameter values
 * @author rodionmoiseev
 */
public class DefaultValueTest {
  @Test
  public void emptyDefaultValueForListParameterStaysEmptyIfNotAssignedOrIsSetOtherwise() {
    MyOptsWithEmptyDefaults opts = new MyOptsWithEmptyDefaults();
    JCommander cmd = new JCommander(opts);
    cmd.parse("-a", "anotherValue");
    Assert.assertEquals(opts.list.size(), 1);
    Assert.assertEquals(opts.list.get(0), "anotherValue");
    Assert.assertEquals(opts.set.size(), 0);
  }

  @Test
  public void defaultValueForListParametersGetsOverwrittenWithSpecifiedValueOrStaysAsDefaultOtherwise() {
    MyOptsWithDefaultValues opts = new MyOptsWithDefaultValues();
    JCommander cmd = new JCommander(opts);
    cmd.parse("-a", "anotherValue");
    Assert.assertEquals(opts.list.size(), 1);
    Assert.assertEquals(opts.list.get(0), "anotherValue");
    Assert.assertEquals(opts.set.size(), 1);
    Assert.assertEquals(opts.set.iterator().next(), "defaultValue");
  }

  @Test
  public void anyNumberOfValuesCanBeSetToListParameters_ForEmptyDefaults(){
    MyOptsWithEmptyDefaults opts = new MyOptsWithEmptyDefaults();
    testSettingMultipleValuesToListTypeParameters(opts);
  }

  @Test
  public void anyNumberOfValuesCanBeSetToListParameters_ForNonEmptyDefaults(){
    MyOptsWithDefaultValues opts = new MyOptsWithDefaultValues();
    testSettingMultipleValuesToListTypeParameters(opts);
  }

  private void testSettingMultipleValuesToListTypeParameters(MyOpts opts) {
    JCommander cmd = new JCommander(opts);
    cmd.parse("-a", "anotherValue", "-a", "anotherValue2",
              "-b", "anotherValue3", "-b", "anotherValue4");
    Assert.assertEquals(opts.list.size(), 2);
    Assert.assertEquals(opts.list.get(0), "anotherValue");
    Assert.assertEquals(opts.list.get(1), "anotherValue2");
    Assert.assertEquals(opts.set.size(), 2);
    Iterator<String> arg2it = opts.set.iterator();
    Assert.assertEquals(arg2it.next(), "anotherValue3");
    Assert.assertEquals(arg2it.next(), "anotherValue4");
  }

  public static class MyOpts {
    @Parameter(names = "-a")
    public List<String> list;
    @Parameter(names = "-b")
    public Set<String> set;
  }

  public static final class MyOptsWithDefaultValues extends MyOpts {
    public MyOptsWithDefaultValues(){
      this.list = singletonList("defaultValue");
      this.set = singletonSet("defaultValue");
    }
  }

  public static final class MyOptsWithEmptyDefaults extends MyOpts {
    public MyOptsWithEmptyDefaults(){
      this.list = Lists.newArrayList();
      this.set = Sets.newLinkedHashSet();
    }
  }

  public static final List<String> singletonList(String value) {
    List<String> list = Lists.newArrayList();
    list.add(value);
    return list;
  }

  public static final Set<String> singletonSet(String value){
    Set<String> set = Sets.newLinkedHashSet();
    set.add(value);
    return set;
  }

  @Test
  public void missingRequiredParameterWithDefaultValueShouldNotRaiseParameterException() {
    class MyRequiredOptsWithDefaultValues {
      @Parameter(names = "-a", required = true)
      public List<String> list = singletonList("defaultValue");
    }

    MyRequiredOptsWithDefaultValues opts = new MyRequiredOptsWithDefaultValues();
    JCommander cmd = new JCommander(opts);
    cmd.parse(new String[]{});
  }

  @Test(expectedExceptions = ParameterException.class)
  public void missingRequiredPrimitiveParameterWithoutDefaultValueMustRaiseParameterException() {
    class MyRequiredOptsWithDefaultValues {
      @Parameter(names = "-i", required = true)
      public int i; // implicit initialization value does not count as a default, so does not satisfy required = true
    }

    MyRequiredOptsWithDefaultValues opts = new MyRequiredOptsWithDefaultValues();
    JCommander cmd = new JCommander(opts);
    cmd.parse(new String[]{});
  }

}
