package com.beust.jcommander.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import com.beust.jcommander.ParameterException;

public class DefaultConsole implements Console {

	private PrintStream out;
	private BufferedReader in;

	public DefaultConsole() {
		try {
			out = new PrintStream(System.out, true, "UTF-8");
			in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			out = System.out;
			in = new BufferedReader(new InputStreamReader(System.in));
		}
	}

	public void print(String msg) {
		out.print(msg);
	}

	public void println(String msg) {
		out.println(msg);
	}

	public char[] readPassword(boolean echoInput) {
		try {
			String result = in.readLine();
			return result.toCharArray();
		} catch (IOException e) {
			throw new ParameterException(e);
		}
	}

}
