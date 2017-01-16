package net.techcable.sonarpet.nms.entity.generators;

import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class EntityWitherPetGenerator extends EntityPetGenerator {
    public EntityWitherPetGenerator(Type currentType, Class<?> hookClass, Class<?> entityClass) {
        super(currentType, hookClass, entityClass);
    }

    @Override
    protected void generate0() {
        super.generate0();
        final String wreakHavocMethodName = Versioning.NMS_VERSION.getObfuscatedMethod("WITHER_WREAK_HAVOC_METHOD");
        // Nope, don't destroy our stuff
        generator.generateMethod(
                (generator) -> {},
                ACC_PUBLIC,
                wreakHavocMethodName,
                Type.VOID_TYPE
        );
    }
}
