package be.kdg.cluedobackend.helpers;

import java.util.Arrays;
import java.util.List;

public final class EnumUtils {
    public static <E extends Enum<?>> List<E> getEnumValues(Class<E> c) {
        return Arrays.asList(c.getEnumConstants());
    }
}
