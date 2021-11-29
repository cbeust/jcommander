package com.beust.jcommander.converters;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.converters.BaseConverter;
import com.beust.jcommander.converters.PathConverter;

/**
 * Converts a string that may be a sequence of paths delimited by a comma
 * ({@code ,}) and optional whitespace into a {@link List} of {@link Path
 * paths}.
 *
 * @author twwwt
 */
public class PathListConverter extends BaseConverter<List<Path>>
{
	private final PathConverter pathConverter;

	public PathListConverter(final String optionName)
	{
		super(optionName);
		pathConverter = new PathConverter(optionName);
	}

	/* @see com.beust.jcommander.IStringConverter#convert(java.lang.String) */
	@Override
	public List<Path> convert(final String value)
	{
		String [] paths = value.split(",");
		List<Path> pathList = new ArrayList<>(paths.length);
		for (String path : paths)
		{
			pathList.add(pathConverter.convert(path.trim()));
		}
		return pathList;
	}
}
