package com.sq022groupA.escalayt.utils;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueIdGenerator {

    private static final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    public static Long generateUniqueLongId() {
        return counter.getAndIncrement();
    }
}
