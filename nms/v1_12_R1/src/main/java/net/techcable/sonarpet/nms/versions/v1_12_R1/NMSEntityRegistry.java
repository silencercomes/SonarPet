package net.techcable.sonarpet.nms.versions.v1_12_R1;

import lombok.*;

import java.lang.invoke.MethodHandle;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RegistryID;
import net.minecraft.server.v1_12_R1.RegistryMaterials;
import net.techcable.pineapple.reflection.PineappleField;
import net.techcable.pineapple.reflection.Reflection;
import net.techcable.sonarpet.nms.EntityRegistry;

public class NMSEntityRegistry implements EntityRegistry {
    //
    // Unlikely to break, even across major versions
    // IE: never broken yet ^_^
    //

    @SuppressWarnings("unchecked")
    private final PineappleField<RegistryMaterials<MinecraftKey, Class<? extends Entity>>, RegistryID<Class<?>>> REGISTRY_ID_FIELD =
            (PineappleField) PineappleField.findFieldWithType(RegistryMaterials.class, RegistryID.class);
    private final MethodHandle REGISTER_ENTITY_METHOD =
            Reflection.getMethod(EntityTypes.class, "a", int.class, String.class, Class.class, String.class); // EntityList.register

    @Override
    @SneakyThrows
    public void registerEntityClass(Class<?> entityClass, String name, int id) {
        REGISTER_ENTITY_METHOD.invokeExact(id, name, entityClass, name);
    }

    @Override
    public void unregisterEntityClass(Class<?> entityClass, String name, int id) {
        // TODO: unregister
    }

    @Override
    public Class<?> getEntityClass(int id) {
        return REGISTRY_ID_FIELD.get(EntityTypes.b).fromId(id);
    }

    @Override
    public int getEntityId(Class<?> entityClass) {
        return REGISTRY_ID_FIELD.get(EntityTypes.b).getId(entityClass);
    }

    @Override
    public String getEntityName(Class<?> entityClass) {
        return EntityTypes.getName(entityClass.asSubclass(Entity.class)).getKey();
    }

    @Override
    public void registerEntityId(int id, Class<?> entityClass) {
        REGISTRY_ID_FIELD.get(EntityTypes.b).a(entityClass, id); // IntIdentityHashBiMap.put
    }

    @Override
    public void unregisterEntityId(int id, Class<?> entityClass) {
        REGISTRY_ID_FIELD.get(EntityTypes.b).a(null, id); // IntIdentityHashBiMap.put
    }
}
