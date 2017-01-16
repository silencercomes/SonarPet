package net.techcable.sonarpet.nms.entity.generators;

import java.lang.invoke.MethodHandle;

import net.techcable.sonarpet.nms.DismountingBlocked;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;
import net.techcable.sonarpet.utils.bytecode.ClassGenerator;
import net.techcable.sonarpet.utils.reflection.Reflection;

import org.bukkit.entity.Player;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;

import static com.google.common.base.Preconditions.*;
import static org.objectweb.asm.Opcodes.*;

public class EntityPetGenerator {
    protected ClassGenerator generator;
    private final Type currentType;
    private final Class<?> hookClass, entityClass;
    private final Type hookType, entityType;

    public EntityPetGenerator(Type currentType, Class<?> hookClass, Class<?> entityClass) {
        this.currentType = currentType;
        this.hookClass = hookClass;
        this.entityClass = entityClass;
        this.hookType = Type.getType(hookClass);
        this.entityType = Type.getType(entityClass);
    }

    /* package */ static class GeneratedPetClassLoader extends ClassLoader {
        protected GeneratedPetClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(Type type, byte[] bytes) {
            return defineClass(type.getClassName(), bytes, 0, bytes.length);
        }
    }

    public Class<?> generateClass() {
        byte[] classfileBytes = this.generate();
        return new GeneratedPetClassLoader(getClass().getClassLoader())
                .defineClass(currentType, classfileBytes);
    }

    private boolean isNeedDismountingBlocked() {
        return Versioning.NMS_VERSION.compareTo(NmsVersion.v1_9_R1) >= 0;
    }

    public byte[] generate() {
        checkState(generator == null, "Already generated class!");
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        generator = new ClassGenerator(writer);
        String[] interfaces;
        if (isNeedDismountingBlocked()) {
            interfaces = new String[] {
                    Type.getInternalName(DismountingBlocked.class)
            };
        } else {
            interfaces = null;
        }
        generator.visit(
                V1_8,
                ACC_PUBLIC | ACC_SYNTHETIC | ACC_SUPER,
                currentType.getInternalName(),
                null,
                entityType.getInternalName(),
                interfaces
        );
        this.generate0();
        generator.visitEnd();
        return writer.toByteArray();
    }

    protected void generate0() {
        generator.generateField(
                ACC_PUBLIC, // Let them set the hook for us
                "hook",
                hookType
        );
        generator.generateConstructor(
                (generator) -> {
                    generator.loadThis();
                    generator.loadArgs();
                    generator.invokeSpecial(
                            "<init>",
                            entityType,
                            Type.VOID_TYPE,
                            WORLD_TYPE
                    );
                    generator.loadThis();
                    generator.visitInsn(ACONST_NULL);
                    generator.visitFieldInsn(
                            PUTFIELD,
                            currentType.getInternalName(),
                            "hook",
                            hookType.getDescriptor()
                    );
                },
                ACC_PUBLIC,
                WORLD_TYPE
        );
        // Generate methods
        final String entityTickMethodName = Versioning.NMS_VERSION.getObfuscatedMethod("ENTITY_TICK_METHOD");
        final String entityMoveMethodName = Versioning.NMS_VERSION.getObfuscatedMethod("ENTITY_MOVE_METHOD");
        final String onStepMethodName = Versioning.NMS_VERSION.getObfuscatedMethod("ON_STEP_METHOD");
        final String onInteractMethodName = Versioning.NMS_VERSION.getObfuscatedMethod("ON_INTERACT_METHOD");
        generator.generateMethod(
                (generator) -> {
                    generator.loadThis();
                    generator.invokeSpecial(
                            entityTickMethodName,
                            entityType,
                            Type.VOID_TYPE
                    );
                    generator.loadThis();
                    generator.getField(
                            currentType,
                            "hook",
                            hookType
                    );
                    generator.invokeVirtual(
                            "onLive",
                            hookType,
                            Type.VOID_TYPE
                    );
                },
                ACC_PUBLIC,
                entityTickMethodName,
                Type.VOID_TYPE
        );
        generator.generateMethod(
                (generator) -> {
                    generator.loadThis();
                    generator.getField(
                            currentType,
                            "hook",
                            hookType
                    );
                    generator.loadArgs();
                    //noinspection deprecation - the new constructor doesn't work
                    generator.visitLdcInsn(new Handle(
                            H_INVOKESPECIAL,
                            entityType.getInternalName(),
                            entityMoveMethodName,
                            Type.getMethodDescriptor(Type.VOID_TYPE, Type.FLOAT_TYPE, Type.FLOAT_TYPE)
                    ));
                    generator.loadThis();
                    generator.invokeVirtual(
                            "bindTo",
                            METHOD_HANDLE_TYPE,
                            METHOD_HANDLE_TYPE,
                            Type.getType(Object.class)
                    );
                    generator.invokeVirtual(
                            "move",
                            hookType,
                            Type.VOID_TYPE,
                            Type.FLOAT_TYPE,
                            Type.FLOAT_TYPE,
                            METHOD_HANDLE_TYPE
                    );
                },
                ACC_PUBLIC,
                entityMoveMethodName,
                Type.VOID_TYPE,
                Type.FLOAT_TYPE,
                Type.FLOAT_TYPE
        );
        generator.generateMethod(
                (generator) -> {
                    generator.loadThis();
                    generator.loadArgs();
                    generator.invokeSpecial(
                            onStepMethodName,
                            entityType,
                            Type.VOID_TYPE,
                            BLOCK_POSITION_TYPE,
                            BLOCK_TYPE
                    );
                    generator.loadThis();
                    generator.getField(
                            currentType,
                            "hook",
                            hookType
                    );
                    for (String coord : new String[] { "X", "Y", "Z" }) {
                        generator.loadArg(0);
                        generator.invokeVirtual(
                                "get" + coord,
                                BLOCK_POSITION_TYPE,
                                Type.INT_TYPE
                        );
                    }
                    generator.invokeVirtual(
                            "onStep",
                            hookType,
                            Type.VOID_TYPE,
                            Type.INT_TYPE,
                            Type.INT_TYPE,
                            Type.INT_TYPE
                    );
                },
                ACC_PUBLIC,
                onStepMethodName,
                Type.VOID_TYPE,
                BLOCK_POSITION_TYPE,
                BLOCK_TYPE
        );
        generator.generateMethod(
                (generator) -> {
                    generator.loadThis();
                    generator.getField(
                            currentType,
                            "hook",
                            hookType
                    );
                    generator.loadArg(0);
                    generator.invokeVirtual(
                            "getBukkitEntity",
                            ENTITY_HUMAN_TYPE,
                            CRAFT_HUMAN_ENTITY_TYPE
                    );
                    generator.visitTypeInsn(CHECKCAST, Type.getInternalName(Player.class));
                    generator.invokeVirtual(
                            "onInteract",
                            hookType,
                            Type.VOID_TYPE,
                            Type.getType(Player.class)
                    );
                },
                ACC_PUBLIC,
                onInteractMethodName,
                Type.VOID_TYPE,
                ENTITY_HUMAN_TYPE
        );
        if (isNeedDismountingBlocked()) {
            // Pets are being secretly dismounted, so we have to block it here
            generator.generateMethod(
                    (generator) -> {},
                    ACC_PUBLIC,
                    "stopRiding",
                    Type.VOID_TYPE
            );
            // Don't mount the stupid boats
            generator.generateMethod(
                    (generator) -> generator.visitInsn(ICONST_0),
                    ACC_PUBLIC,
                    "startRiding",
                    Type.BOOLEAN_TYPE,
                    ENTITY_TYPE
            );
            // Provide a bypass so people can actually stop riding us
            generator.generateMethod(
                    (generator) -> {
                        generator.loadThis();
                        generator.invokeSpecial(
                                "stopRiding",
                                entityType,
                                Type.VOID_TYPE
                        );
                    },
                    ACC_PUBLIC,
                    "reallyStopRiding",
                    Type.VOID_TYPE
            );
        }
    }

    //
    // Type constants
    //
    private static final Type METHOD_HANDLE_TYPE = Type.getType(MethodHandle.class);
    private static final Type BLOCK_POSITION_TYPE = Type.getType(Reflection.getNmsClass("BlockPosition"));
    private static final Type BLOCK_TYPE = Type.getType(Reflection.getNmsClass("Block"));
    private static final Type ENTITY_TYPE = Type.getType(Reflection.getNmsClass("Entity"));
    private static final Type ENTITY_HUMAN_TYPE = Type.getType(Reflection.getNmsClass("EntityHuman"));
    private static final Type CRAFT_HUMAN_ENTITY_TYPE = Type.getType(Reflection.getObcClass("entity.CraftHumanEntity"));
    private static final Type WORLD_TYPE = Type.getType(Reflection.getNmsClass("World"));

}
