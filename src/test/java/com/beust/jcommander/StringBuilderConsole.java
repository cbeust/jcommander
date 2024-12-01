package com.beust.jcommander;

import com.beust.jcommander.internal.Console;

public class StringBuilderConsole implements Console {

	public final StringBuilder output;

	public StringBuilderConsole(StringBuilder output) {
		this.output = output;
	}

	@Override
	public void print(CharSequence msg) {
		output.append(msg);
	}

	@Override
	public void println(CharSequence msg) {
		print(msg);
		output.append('\n');
	}

	@Override
	public char[] readPassword(boolean echoInput) {
		throw new UnsupportedOperationException();
	}

}
