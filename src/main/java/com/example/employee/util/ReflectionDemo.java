package com.example.employee.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a private JDK field via reflection.
 * LEGACY: works on Java 8 (and 9-15 with only a warning). On Java 16+ it fails with InaccessibleObjectException
 * unless the JVM is started with --add-opens java.base/java.util=ALL-UNNAMED.
 */
public final class ReflectionDemo {

    private ReflectionDemo() {
    }

    public static int internalSize(ArrayList<?> list) throws ReflectiveOperationException {
        Field f = ArrayList.class.getDeclaredField("size");
        f.setAccessible(true);
        return f.getInt(list);
    }

    public static int internalSizeOf(List<?> list) throws ReflectiveOperationException {
        return internalSize((ArrayList<?>) list);
    }
}
