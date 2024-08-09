package com.beust.jcommander;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Strings {

    public static boolean isStringEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean startsWith(String s, String with, boolean isCaseSensitive) {
        return isCaseSensitive ? s.startsWith(with) : s.toLowerCase().startsWith(with.toLowerCase());
    }

    public static String join(String delimiter, List<String> args) {
        return String.join(delimiter, args);
    }

    public static String join(String delimiter, Object[] args) {
        return Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(delimiter));
    }
}
