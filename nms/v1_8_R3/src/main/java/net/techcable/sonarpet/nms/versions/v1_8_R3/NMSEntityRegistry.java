package net.techcable.sonarpet.nms.versions.v1_8_R3;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import net.minecraft.server.v1_8_R3.EntityTypes;
import net.techcable.pineapple.reflection.PineappleField;
import net.techcable.sonarpet.nms.EntityRegistry;

public class NMSEntityRegistry implements EntityRegistry {
    private static final PineappleField<?, Map<Class<?>, String>> CLASS_TO_NAME_FIELD;
    private static final PineappleField<?, Map<Integer, Class<?>>> ID_TO_CLASS_FIELD;
    private static final PineappleField<?, Map<Class<?>, Integer>> CLASS_TO_ID_FIELD;
    static {
        ImmutableList<PineappleField<EntityTypes, Map>> mapFields = PineappleField.findFieldsWithType(EntityTypes.class, Map.class);
        assert mapFields.size() == 5;
        //noinspection unchecked
        CLASS_TO_NAME_FIELD = (PineappleField) mapFields.get(1);
        //noinspection unchecked
        ID_TO_CLASS_FIELD = (PineappleField) mapFields.get(2);
        //noinspection unchecked
        CLASS_TO_ID_FIELD = (PineappleField) mapFields.get(3);
    }

    @Override
    public void registerEntityClass(Class<?> entityClass, String name, int id) {
        CLASS_TO_NAME_FIELD.getStatic().put(entityClass, name);
        CLASS_TO_ID_FIELD.getStatic().put(entityClass, id);
    }

    @Override
    public void unregisterEntityClass(Class<?> entityClass, String name, int id) {
        String actualName = CLASS_TO_NAME_FIELD.getStatic().remove(entityClass);
        Integer actualId = CLASS_TO_ID_FIELD.getStatic().remove(entityClass);
        if (!Objects.equals(name, actualName)) {
            throw new IllegalArgumentException("Expected name " + name + " didn't equal actual name " + Objects.toString(actualName));
        } else if (actualId == null || actualId != id) {
            throw new IllegalArgumentException("Expected id " + id + " didn't equal actual id " + Objects.toString(actualId));
        }
    }

    @Override
    public Class<?> getEntityClass(int id) {
        return ID_TO_CLASS_FIELD.getStatic().get(id);
    }

    @Override
    public void registerEntityId(int id, Class<?> entityClass) {
        ID_TO_CLASS_FIELD.getStatic().put(id, entityClass);
    }

    @Override
    public void unregisterEntityId(int id, Class<?> entityClass) {
        Class<?> actualEntityClass = ID_TO_CLASS_FIELD.getStatic().remove(id);
        if (!Objects.equals(actualEntityClass, entityClass)) {
            throw new IllegalArgumentException("Expected entity class " + entityClass + " didn't equal actual entity class " + Objects.toString(actualEntityClass));
        }
    }
}
