package com.codzzz.lang.easy.statemachine.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Asserts {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}