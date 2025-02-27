package dev.dpvb.util;

import java.util.Optional;

public final class NumberUtil {

    public static Optional<Integer> safeParseInt(String num) {
        try {
            return Optional.of(Integer.parseInt(num));
        } catch (NumberFormatException ignore) {
            return Optional.empty();
        }
    }

}
