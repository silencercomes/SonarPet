package net.techcable.sonarpet.compat;

import lombok.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;

import net.techcable.pineapple.reflection.PineappleField;
import net.techcable.pineapple.reflection.Reflection;
import net.techcable.sonarpet.nms.EntityRegistry;
import net.techcable.sonarpet.utils.Versioning;
import net.techcable.sonarpet.utils.reflection.MinecraftReflection;

import static java.util.Objects.*;

public class CitizensEntityRegistryHack implements EntityRegistry {
    private final MethodHandle PUT_METHOD, MINECRAFT_KEY_CONSTRUCTOR, GET_ID_METHOD,
            FROM_ID_METHOD, ENTITY_TYPES_GET_NAME_METHOD, REGISTRY_ID_PUT_METHOD;
    private final PineappleField<Object, Map> ENTITY_ID_MAP_FIELD;
    private final PineappleField<Object, String> MINECRAFT_KEY_NAME_FIELD;
    private final PineappleField WRAPPED_REGISTRY_FIELD, REGISTRY_ID_FIELD;
    private final Class<?> MINECRAFT_KEY_CLASS, REGISTRY_MATERIALS_CLASS, REGISTRY_ID_CLASS,
            ENTITY_TYPES_CLASS;
    private final Object registryInstance;
    @SuppressWarnings("unchecked")
    @SneakyThrows({NoSuchMethodException.class, IllegalAccessException.class})
    private CitizensEntityRegistryHack() {
        if (!isNeeded()) throw new IllegalStateException();
        // Fetch some needed NMS classes and methods
        MINECRAFT_KEY_CLASS = MinecraftReflection.findNmsClass("MinecraftKey");
        REGISTRY_MATERIALS_CLASS = MinecraftReflection.findNmsClass("RegistryMaterials");
        REGISTRY_ID_CLASS = MinecraftReflection.findNmsClass("RegistryID");
        ENTITY_TYPES_CLASS = MinecraftReflection.findNmsClass("EntityTypes");
        MINECRAFT_KEY_CONSTRUCTOR = Reflection.getConstructor(MINECRAFT_KEY_CLASS, String.class);
        REGISTRY_ID_FIELD = PineappleField.findFieldWithType(REGISTRY_MATERIALS_CLASS, REGISTRY_ID_CLASS);
        GET_ID_METHOD = Reflection.getMethod(REGISTRY_ID_CLASS, "getId", Object.class);
        FROM_ID_METHOD = Reflection.getMethod(REGISTRY_ID_CLASS, "fromId", int.class);
        ImmutableList<PineappleField<Object, String>> minecraftKeyFields = PineappleField.findFieldsWithType((Class) MINECRAFT_KEY_CLASS, String.class);
        Verify.verify(minecraftKeyFields.size() == 2, "MinecraftKey should have two String fields: %s", minecraftKeyFields);
        MINECRAFT_KEY_NAME_FIELD = minecraftKeyFields.get(1);
        REGISTRY_ID_PUT_METHOD = Reflection.getMethod(REGISTRY_ID_CLASS, "a", Object.class, int.class);
        // Static method that forwards to the CustomEntityRegistry
        ENTITY_TYPES_GET_NAME_METHOD = MethodHandles.publicLookup().findStatic(
                ENTITY_TYPES_CLASS,
                "getName",
                MethodType.methodType(
                        MINECRAFT_KEY_CLASS,
                        Class.class
                )
        );
        // Now that we know the class is there, fetch the methods we need
        PUT_METHOD = Reflection.getMethod(
                CITIZENS_REGISTRY_CLASS,
                "put",
                int.class,
                MINECRAFT_KEY_CLASS,
                Class.class
        );
        // Fetch the internal fields
        ENTITY_ID_MAP_FIELD = PineappleField.create(
                CITIZENS_REGISTRY_CLASS,
                "entityIds",
                Map.class
        );
        WRAPPED_REGISTRY_FIELD = PineappleField.create(
                CITIZENS_REGISTRY_CLASS,
                "wrapped",
                REGISTRY_MATERIALS_CLASS
        );
        // Now look for the instance
        registryInstance = requireNonNull(findRegistryObject());
    }

    @Override
    @SneakyThrows
    public void registerEntityClass(Class<?> entityClass, String name, int id) {
        Object key = MINECRAFT_KEY_CONSTRUCTOR.invoke(name);
        MINECRAFT_KEY_CLASS.cast(key);
        PUT_METHOD.invoke(registryInstance, id, key, entityClass);
    }

    @Override
    public void unregisterEntityClass(Class<?> entityClass, String name, int id) {
        // TODO: unregister
    }

    @Override
    @SneakyThrows
    public Class<?> getEntityClass(int id) {
        /*
         * NOTE: The mappings are no longer bidirectional now that citizens is in charge.
         * However, the underlying NMS mappings are, so we can still use those.
         */
        return (Class<?>) FROM_ID_METHOD.invoke(getRegistryId(), id);
    }

    @SuppressWarnings("unchecked")
    private Object getRegistryId() {
        Object nmsRegistry = Objects.requireNonNull(WRAPPED_REGISTRY_FIELD.get(registryInstance), "Null wrapped registry");
        return Objects.requireNonNull(REGISTRY_ID_FIELD.get(nmsRegistry), "Null RegistryID");
    }

    @SuppressWarnings("unchecked")
    @Override
    @SneakyThrows
    public int getEntityId(Class<?> entityClass) {
        // NOTE: Check citizens first
        Map<Class<?>, Integer> m = ENTITY_ID_MAP_FIELD.get(registryInstance);
        if (m.containsKey(entityClass)) {
            return m.get(entityClass);
        }
        // Fallback to NMS (just like citizens does)
        return (int) GET_ID_METHOD.invoke(getRegistryId(), entityClass);
    }

    @Override
    @SneakyThrows
    public String getEntityName(Class<?> entityClass) {
        Object key = ENTITY_TYPES_GET_NAME_METHOD.invoke(entityClass);
        if (key == null) throw new IllegalArgumentException("Unknown entity class: " + entityClass);
        return MINECRAFT_KEY_NAME_FIELD.get(key);
    }

    @Override
    @SneakyThrows
    public void registerEntityId(int id, Class<?> entityClass) {
        REGISTRY_ID_PUT_METHOD.invoke(getRegistryId(), entityClass, id);
    }

    @Override
    @SneakyThrows
    public void unregisterEntityId(int id, Class<?> entityClass) {
        REGISTRY_ID_PUT_METHOD.invoke(getRegistryId(), null, id);
    }

    private static final Class<?> CITIZENS_REGISTRY_CLASS = Reflection.getClass(
            "net.citizensnpcs.nms." + Versioning.NMS_VERSION_STRING + ".util.CustomEntityRegistry"
    );
    public static boolean isNeeded() {
        return CITIZENS_REGISTRY_CLASS != null;
    }
    private static CitizensEntityRegistryHack INSTANCE;
    @SneakyThrows(IllegalAccessException.class) // Shouldn't happen
    private static Object findRegistryObject() {
        Class<?> entityTypesClass = MinecraftReflection.getNmsClass("EntityTypes");
        // Find the first (and hopefully only) non-null static field in 'EntityTypes' that is instanceof CITIZENS_REGISTRY_CLASS
        for (Field field : entityTypesClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && field.getType().isAssignableFrom(CITIZENS_REGISTRY_CLASS)) {
                field.setAccessible(true);
                Object value = field.get(null);
                if (value != null && CITIZENS_REGISTRY_CLASS.isInstance(value)) {
                    return CITIZENS_REGISTRY_CLASS.cast(value);
                }
            }
        }
        throw new IllegalStateException("Couldn't locate citizen's custom registry " + CITIZENS_REGISTRY_CLASS + " in order to hack compatibility");
    }
    @Nonnull
    public static CitizensEntityRegistryHack createInstance() {
        if (!isNeeded()) throw new IllegalStateException("Hack not needed!");
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (CitizensEntityRegistryHack.class) {
            if (INSTANCE == null) {
                INSTANCE = new CitizensEntityRegistryHack();
            }
            return INSTANCE;
        }
    }
}
