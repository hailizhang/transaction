package com.zhl.transaction.util;

import java.util.Objects;

public class AssertUtil {

    public static void isTrue(Boolean expression, RuntimeException e) {
        if (!expression) {
            throw e;
        }
    }

    public static void notNull(Object object, RuntimeException e) {
        if (Objects.isNull(object)) {
            throw e;
        }
    }
}
