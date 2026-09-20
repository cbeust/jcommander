package com.beust.jcommander;

import java.util.Locale;

public final class LocaleTestHelper {
    private static final Object DEFAULT_LOCALE_LOCK = new Object();

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    private LocaleTestHelper() {
    }

    public static void withDefaultLocale(Locale locale, ThrowingRunnable action) {
        synchronized (DEFAULT_LOCALE_LOCK) {
            Locale original = Locale.getDefault();
            try {
                Locale.setDefault(locale);
                action.run();
            } catch (Exception e) {
                LocaleTestHelper.<RuntimeException>sneakyThrow(e);
            } finally {
                Locale.setDefault(original);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception> void sneakyThrow(Exception e) throws E {
        throw (E) e;
    }
}
