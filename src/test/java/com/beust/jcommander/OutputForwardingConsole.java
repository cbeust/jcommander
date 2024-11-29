package com.beust.jcommander;

import com.beust.jcommander.internal.Console;

public class OutputForwardingConsole implements Console {

	public final StringBuilder output;

	public OutputForwardingConsole(StringBuilder output) {
		this.output = output;
	}

	@Override
	public void print(String msg) {
		output.append(msg);
	}

	@Override
	public void println(String msg) {
		print(msg);
		output.append("\n");
	}

	@Override
	public char[] readPassword(boolean echoInput) {
		throw new UnsupportedOperationException();
	}

}
