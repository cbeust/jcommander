/**
 * Copyright (C) 2011 the original author or authors.
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

package com.beust.jcommander.validators;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.mockito.Mockito;

import com.beust.jcommander.ParameterException;

public class FileExistsValidatorTest {

  @Test(expectedExceptions=ParameterException.class)
  public void throwsParameterExceptionIfFileDoesNotExist() {
    FileExistsValidator validator = Mockito.spy(new FileExistsValidator());

    File mockFile = Mockito.mock(File.class);
    Mockito.doReturn(mockFile).when(validator).getFile(Mockito.anyString());

    validator.validate(randomString(), randomString());
  }

  @Test
  public void validatesThatTheFileExists() throws IOException {
    File tmpFile = new File("target/"+randomString());
    Assert.assertTrue(tmpFile.createNewFile(), "Failed to create temp file to perform test.");

    FileExistsValidator validator = new FileExistsValidator();

    validator.validate(randomString(), tmpFile.getAbsolutePath());
  }

  private String randomString() {
    return UUID.randomUUID().toString();
  }

}
