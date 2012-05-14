package com.beust.jcommander;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

public abstract class Messages {

	private static ResourceBundle messages = ResourceBundle
			.getBundle("com.beust.jcommander.intl.Messages");

	public static String getMsg(String key, Object... values) {
		return MessageFormat.format(messages.getString(key), values);
	}

	public static String getPassMsg(Throwable e, String key, Object... values) {
		return getMsg(key, values) + ": " + e.getMessage();
	}

	public static String join(String separator, Collection<?> c) {
		if (c == null || c.size() == 0) {
			return "";
		}

		Iterator<?> it = c.iterator();
		StringBuffer buffer = new StringBuffer(it.next().toString());

		while (it.hasNext()) {
			buffer.append(separator);
			buffer.append(it.next().toString());
		}

		return buffer.toString();
	}

	public static String join(String separator, Object[] arr) {
		if (arr == null || arr.length == 0) {
			return "";
		}

		StringBuffer buffer = new StringBuffer(arr[0].toString());

		int i = 1;
		while (i < arr.length) {
			buffer.append(separator);
			buffer.append(arr[i++]);
		}

		return buffer.toString();
	}
}
