package net.techcable.sonarpet.nms.entity.generators;

import net.techcable.sonarpet.utils.Versioning;

import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class EntityUndeadPetGenerator extends EntityPetGenerator {
    public EntityUndeadPetGenerator(Type currentType, Class<?> hookClass, Class<?> entityClass) {
        super(currentType, hookClass, entityClass);
    }

    @Override
    protected void generate0() {
        super.generate0();
       // Prevent the pet from burning
        generator.generateMethod(
                (generator) -> {},
                ACC_PUBLIC,
                "setOnFire",
                Type.VOID_TYPE,
                Type.INT_TYPE
        );
    }
}
