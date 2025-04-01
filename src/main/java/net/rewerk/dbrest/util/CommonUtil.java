package net.rewerk.dbrest.util;

import java.util.HashSet;
import java.util.List;

public abstract class CommonUtil {
    public static boolean isListEquals(List<?> list1, List<?> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }
}
