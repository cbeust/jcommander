package com.beust.jcommander.converters;

import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.converters.IParameterSplitter;

/**
 * Parameter splitter that does not split parameters. Just wraps the parameter
 * as single element into a new instance of a {@link List}.
 * 
 * @author schnatterer
 * 
 */
public class NoParameterSplitter implements IParameterSplitter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.beust.jcommander.converters.IParameterSplitter#split(java.lang.String
	 * )
	 */
	@Override
	public List<String> split(final String value) {
		List<String> result = new LinkedList<String>();
		result.add(value);
		return result;
	}
}
