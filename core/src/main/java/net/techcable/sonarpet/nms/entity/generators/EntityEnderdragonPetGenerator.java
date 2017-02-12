package net.techcable.sonarpet.nms.entity.generators;

import net.techcable.sonarpet.nms.INMS;
import net.techcable.sonarpet.nms.NMSEntity;
import net.techcable.sonarpet.nms.NMSLivingEntity;
import net.techcable.sonarpet.utils.Versioning;
import net.techcable.sonarpet.utils.reflection.MinecraftReflection;

import org.bukkit.entity.Entity;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class EntityEnderdragonPetGenerator extends EntityPetGenerator {
    public EntityEnderdragonPetGenerator(Type currentType, Class<?> hookClass, Class<?> entityClass) {
        super(currentType, hookClass, entityClass);
    }

    @Override
    protected void generate0() {
        super.generate0();
        final String livingUpdateMethod = Versioning.NMS_VERSION.getObfuscatedMethod("LIVING_UPDATE_METHOD");
        // Nope, don't destroy our stuff
        generator.generateMethod(
                (generator) -> {},
                ACC_PUBLIC,
                livingUpdateMethod,
                Type.VOID_TYPE
        );
    }
}
