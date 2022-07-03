package com.codzzz.lang.easy.statemachine.utils;

@UtilityClass
public class ClassUtil {

    /**
     * May cause {@link ClassCastException} . Be caution to use it.
     */
    @SuppressWarnings("unchecked")
    public <B> B cast(Object a) {
        return (B) a;
    }
}
