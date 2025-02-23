package dev.dpvb.util;

import java.util.Optional;

public final class NumberUtil {

    private NumberUtil() {
        throw new IllegalStateException("Utility Class");
    }

    public static Optional<Integer> safeParseInt(String num) {
        try {
            return Optional.of(Integer.parseInt(num));
        } catch (NumberFormatException ignore) {
            return Optional.empty();
        }
    }

}
