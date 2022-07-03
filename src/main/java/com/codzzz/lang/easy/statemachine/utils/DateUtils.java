package com.codzzz.lang.easy.statemachine.utils;

@UtilityClass
public class DateUtils {

    /**
     * 当前时间戳
     *
     * @return -
     */
    public static long nowTimeStamp() {
        return Instant.now().toEpochMilli();
    }
}
