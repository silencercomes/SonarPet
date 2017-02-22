package net.techcable.sonarpet.nms.entity.generators;

import net.techcable.sonarpet.utils.Versioning;
import net.techcable.sonarpet.utils.reflection.MinecraftReflection;

import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * Some entity specific logic is kept in the living update/motion method, like enderdragon destruction.
 * This generator overrides the default (entity-specific) update logic, with a minimalistic alternative.
 */
public class CustomMotionPetGenerator extends EntityPetGenerator {
    public CustomMotionPetGenerator(Type currentType, Class<?> hookClass, Class<?> entityClass) {
        super(currentType, hookClass, entityClass);
    }

    @Override
    protected void generate0() {
        super.generate0();
        final String livingUpdateMethod = Versioning.NMS_VERSION.getObfuscatedMethod("LIVING_UPDATE_METHOD");
        // Nope, don't move funky
        generator.generateMethod(
                (generator) -> {
                    // TODO: Fix the bug with these pets not actually moving (maybe something to do with a missing setPosition?)
                    // this.doTick()
                    generator.loadThis();
                    generator.invokeVirtual(
                            "doTick",
                            ENTITY_LIVING_TYPE,
                            Type.VOID_TYPE
                    );
                    final String sidewaysMotionField = Versioning.NMS_VERSION.getObfuscatedField("ENTITY_SIDEWAYS_MOTION_FIELD");
                    final String forwardMotionField = Versioning.NMS_VERSION.getObfuscatedField("ENTITY_FORWARD_MOTION_FIELD");
                    // this.moveStrafing *= 0.98
                    // this.moveForward *= 0.98
                    for (String fieldName : new String[] {sidewaysMotionField, forwardMotionField}) {
                        generator.loadThis();
                        generator.visitInsn(DUP);
                        generator.getField(ENTITY_LIVING_TYPE, fieldName, Type.FLOAT_TYPE);
                        generator.visitLdcInsn(0.98f);
                        generator.visitInsn(FMUL);
                        generator.putField(ENTITY_LIVING_TYPE, fieldName, Type.FLOAT_TYPE);
                    }
                    // this.move(this.moveStrafing, this.moveForward)
                    generator.loadThis();
                    generator.visitInsn(DUP);
                    generator.getField(ENTITY_LIVING_TYPE, sidewaysMotionField, Type.FLOAT_TYPE);
                    generator.loadThis();
                    generator.getField(ENTITY_LIVING_TYPE, forwardMotionField, Type.FLOAT_TYPE);
                    generator.invokeVirtual(
                            Versioning.NMS_VERSION.getObfuscatedMethod("ENTITY_MOVE_METHOD"),
                            ENTITY_LIVING_TYPE,
                            Type.VOID_TYPE,
                            Type.FLOAT_TYPE,
                            Type.FLOAT_TYPE
                    );
                },
                ACC_PUBLIC,
                livingUpdateMethod,
                Type.VOID_TYPE
        );
    }

    //
    // Type constants
    //
    private static final Type ENTITY_LIVING_TYPE = Type.getType(MinecraftReflection.getNmsClass("EntityLiving"));
}
