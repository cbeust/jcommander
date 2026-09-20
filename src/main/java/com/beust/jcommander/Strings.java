package com.beust.jcommander;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class Strings {

    public static boolean isStringEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean startsWith(String s, String with, boolean isCaseSensitive) {
        return isCaseSensitive ? s.startsWith(with) : toLowerCase(s).startsWith(toLowerCase(with));
    }

    public static String toLowerCase(String s) {
        return s.toLowerCase(Locale.ROOT);
    }

    public static String join(String delimiter, Object[] args) {
        return Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(delimiter));
    }
}
