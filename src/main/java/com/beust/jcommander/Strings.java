package com.beust.jcommander;

public class Strings {

    public static boolean isStringEmpty(String s) {
        return s == null || "".equals(s);
    }

    public static boolean startsWith(String s, String with, boolean isCaseSensitive) {
        if (isCaseSensitive)
            return s.startsWith(with);
        else {
            return s.toLowerCase().startsWith(with.toLowerCase());
        }
    }
}
