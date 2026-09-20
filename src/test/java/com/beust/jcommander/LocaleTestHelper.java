package com.beust.jcommander;

import java.util.Locale;

public final class LocaleTestHelper {
    private static final Object DEFAULT_LOCALE_LOCK = new Object();

    private LocaleTestHelper() {
    }

    public static void withDefaultLocale(Locale locale, Runnable action) {
        synchronized (DEFAULT_LOCALE_LOCK) {
            Locale original = Locale.getDefault();
            try {
                Locale.setDefault(locale);
                action.run();
            } finally {
                Locale.setDefault(original);
            }
        }
    }
}
