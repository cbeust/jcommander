package com.beust.jcommander.internal;

import java.io.IOException;
import java.io.InputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class DefaultConsoleTest {
  public void readPasswordCanBeCalledMultipleTimes() {
    final InputStream inBackup = System.in;
    try {
      final StringInputStream in = new StringInputStream();
      System.setIn(in);
      final Console console = new DefaultConsole();

      in.setData("password1\n");
      char[] password = console.readPassword(false);
      Assert.assertEquals(password, "password1".toCharArray());
      Assert.assertFalse(in.isClosedCalled(), "System.in stream shouldn't be closed");

      in.setData("password2\n");
      password = console.readPassword(false);
      Assert.assertEquals(password, "password2".toCharArray());
      Assert.assertFalse(in.isClosedCalled(), "System.in stream shouldn't be closed");
    } finally {
      System.setIn(inBackup);
    }
  }

  private static class StringInputStream extends InputStream {
    private byte[] data = new byte[0];
    private int offset = 0;
    private boolean closedCalled;

    StringInputStream() {
      super();
    }

    void setData(final String strData) {
      data = strData.getBytes();
      offset = 0;
    }

    boolean isClosedCalled() {
      return closedCalled;
    }

    @Override
    public int read() throws IOException {
      if (offset >= data.length) {
        return -1;
      }
      return 0xFFFF & data[offset++];
    }

    @Override
    public void close() throws IOException {
      closedCalled = true;
      super.close();
    }
  }
}
