package net.techcable.sonarpet.utils.reflection;

import lombok.*;

import static net.techcable.sonarpet.utils.Versioning.NMS_PACKAGE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Reflection {
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T> Class<? extends T> getClass(String name, Class<T> clazz) {
        try {
            return Class.forName(name).asSubclass(clazz);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getNmsClass(String id) {
        return getClass(NMS_PACKAGE + "." + id);
    }

    public static <T> Class<? extends T> getNmsClass(String id, Class<T> clazz) {
        return getClass(NMS_PACKAGE + "." + id, clazz);
    }
}
