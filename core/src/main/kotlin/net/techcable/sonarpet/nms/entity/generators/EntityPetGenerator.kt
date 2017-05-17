package net.techcable.sonarpet.nms.entity.generators

import com.dsh105.echopet.compat.api.entity.IEntityPet
import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin
import com.google.common.collect.ImmutableList
import net.techcable.sonarpet.nms.DismountingBlocked
import net.techcable.sonarpet.nms.NMSInsentientEntity
import net.techcable.sonarpet.nms.NMSPetEntity
import net.techcable.sonarpet.nms.entity.generators.GeneratedMethod.Companion.createGetter
import net.techcable.sonarpet.utils.*
import net.techcable.sonarpet.utils.bytecode.ClassGenerator
import net.techcable.sonarpet.utils.bytecode.MethodGenerator
import net.techcable.sonarpet.utils.bytecode.MethodGenerator.*
import net.techcable.sonarpet.utils.reflection.MinecraftReflection
import org.bukkit.entity.Player
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.Type.*
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.PrintWriter
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

open class GenerationState(
        val currentType: Type,
        val generator: ClassGenerator
) {
    fun MethodGenerator.generateCheckSanity() {
        loadThis()
        invokeVirtual("checkSanity", currentType, VOID_TYPE)
    }
}

class GeneratedConstructor(
        parameterTypes: List<Type>,
        val access: Int = ACC_PUBLIC,
        val generator: MethodGenerator.(GenerationState) -> Unit
) {
    val parameterTypes = parameterTypes.toImmutableList() // Paranoid
    fun generate(state: GenerationState) {
        state.generator.generateConstructor(
                Consumer { generator(it, state) },
                access,
                *parameterTypes.toTypedArray()
        )
    }
}

class GeneratedField(val name: String, val type: Type, val access: Int = ACC_PRIVATE) {
    fun generate(state: GenerationState) = state.generator.generateField(access, name, type)
}

private val dumpClasses = SystemProperties.getBoolean("sonarpet.dumpClasses") ?: false
open class EntityPetGenerator(
        protected val plugin: IEchoPetPlugin,
        protected val currentType: Type,
        protected val hookClass: Class<*>,
        entityClass: Class<*>
) {
    protected val hookType = getType(hookClass)!!
    protected val entityType = getType(entityClass)!!
    private val startedGeneration = AtomicBoolean(false)

    private class GeneratedPetClassLoader(parent: ClassLoader) : ClassLoader(parent) {
        fun defineClass(type: Type, bytes: ByteArray): Class<*> {
            return defineClass(type.className, bytes, 0, bytes.size)
        }
    }

    fun generateClass(): Class<*> {
        plugin.logger.fine("Generating " + hookClass.simpleName)
        val classfileBytes = this.generate()
        return GeneratedPetClassLoader(javaClass.classLoader)
                .defineClass(currentType, classfileBytes)
    }

    val isNeedDismountingBlocked
        get() = Versioning.NMS_VERSION >= NmsVersion.v1_9_R1


    private fun generate(): ByteArray {
        check(startedGeneration.compareAndSet(false, true)) { "Already generated class!" }
        val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)
        val printer = if (dumpClasses) Textifier() else null
        val visitor = if (printer != null) TraceClassVisitor(writer, printer, null) else writer
        val generator = ClassGenerator(visitor)
        val state = GenerationState(currentType, generator)
        generator.visit(
                V1_8,
                ACC_PUBLIC or ACC_SYNTHETIC or ACC_SUPER,
                currentType.internalName, null,
                entityType.internalName,
                implementedInterfaces.map { it.internalName }.toTypedArray()
        )
        generatedFields.forEach { it.generate(state) }
        generatedConstructors.forEach { it.generate(state) }
        generatedMethods.forEach { it.generate(state) }
        generator.visitEnd()
        if (printer != null) {
            plugin.scheduleTask(async = true) {
                val file = File(plugin.dataFolder, "generated-classes/" + currentType.className + ".txt")
                file.parentFile.mkdirs()
                file.createNewFile()
                file.bufferedWriter().use {
                    printer.print(PrintWriter(it))
                }
            }
        }
        return writer.toByteArray()
    }

    protected open val implementedInterfaces: ImmutableList<Type>
        get() = buildImmutableList {
            add(NMSPetEntity::class.asmType)
            if (isNeedDismountingBlocked) {
                add(DismountingBlocked::class.asmType)
            }
        }

    protected open val generatedFields: ImmutableList<GeneratedField>
        get() = immutableListOf(GeneratedField(name = "hook", type = hookType, access = ACC_PUBLIC))

    protected open val generatedConstructors: ImmutableList<GeneratedConstructor>
        get() = immutableListOf(GeneratedConstructor(parameterTypes = listOf(WORLD_TYPE)) { _ ->
            loadThis()
            loadArgs()
            invokeSpecial("<init>", entityType, VOID_TYPE, WORLD_TYPE) // super(world)
            loadThis()
            visitInsn(ACONST_NULL)
            visitFieldInsn(PUTFIELD, currentType.internalName, "hook", hookType.descriptor)
        })

    protected open val generatedMethods: ImmutableList<GeneratedMethod>
        get() = buildImmutableList {
            add(createGetter("hook", hookType, returnType = IEntityPet::class.asmType))
            add(GeneratedMethod(entityTickMethodName) {
                loadThis()
                invokeSpecial(entityTickMethodName, entityType, VOID_TYPE)
                loadThis()
                getField(currentType, "hook", hookType)
                invokeVirtual("onLive", hookType, VOID_TYPE)
            })
            add(GeneratedMethod(entityMoveMethodName, parameterTypes = entityMoveMethodParameters.toList()) {
                loadThis()
                getField(currentType, "hook", hookType)
                loadArgs()
                //noinspection deprecation - the new constructor doesn't work
                visitLdcInsn(Handle(
                        H_INVOKESPECIAL,
                        entityType.internalName,
                        entityMoveMethodName,
                        getMethodDescriptor(VOID_TYPE, *entityMoveMethodParameters),
                        false
                ))
                loadThis()
                invokeVirtual("bindTo", METHOD_HANDLE_TYPE, METHOD_HANDLE_TYPE, OBJECT_TYPE)
                invokeVirtual("move", hookType, VOID_TYPE, *entityMoveMethodParameters, METHOD_HANDLE_TYPE)
            })
            add(GeneratedMethod(onStepMethodName, parameterTypes = listOf(BLOCK_POSITION_TYPE, BLOCK_TYPE)) { _ ->
                loadThis()
                loadArgs()
                invokeSpecial(onStepMethodName, entityType, VOID_TYPE, BLOCK_POSITION_TYPE, BLOCK_TYPE)
                loadThis()
                getField(currentType, "hook", hookType)
                for (coord in arrayOf("X", "Y", "Z")) {
                    loadArg(0)
                    invokeVirtual("get$coord", BLOCK_POSITION_TYPE, INT_TYPE)
                }
                invokeVirtual("onStep", hookType, VOID_TYPE, INT_TYPE, INT_TYPE, INT_TYPE)
            })
            add(GeneratedMethod(onInteractMethodName, parameterTypes = listOf(ENTITY_HUMAN_TYPE)) { _ ->
                loadThis()
                getField(currentType, "hook", hookType)
                loadArg(0)
                invokeVirtual("getBukkitEntity", ENTITY_HUMAN_TYPE, CRAFT_HUMAN_ENTITY_TYPE)
                visitTypeInsn(CHECKCAST, Player::class.internalName)
                invokeVirtual("onInteract", hookType, VOID_TYPE, Player::class.asmType)
            })
            /*
             * Block the 'procedural' AI system too, which is different from the usual API.
             * This prevents stuff like bat flying, villager trading, and wither chaos.
             */
            add(GeneratedMethod.noOp(proceduralAIMethodName))
            if (isNeedDismountingBlocked) {
                // Pets are being secretly dismounted, so we have to block it here
                add(GeneratedMethod.noOp("stopRiding"))
                // Don't mount the stupid boats
                add(GeneratedMethod.returnConstant("startRiding", false, parameterTypes = listOf(ENTITY_TYPE)))
                // Provide a bypass so people can actually stop riding us
                add(GeneratedMethod("reallyStopRiding") {
                    loadThis()
                    invokeSpecial("stopRiding", entityType, VOID_TYPE)
                })
            }
            // Ensures that this.hook != null && this.hook.getEntity().getHandle() == this
            add(GeneratedMethod("checkSanity", invokeCheckSanity = false, access = ACC_PRIVATE) {
                loadThis()
                getField(currentType, "hook", hookType)
                visitInsn(DUP)
                runIf(IFNULL) {
                    loadThis()
                    throwFormattedException(NullPointerException::class.java, "{} has null hook!", 1)
                }
                // Load hook.getEntity().getHandle()
                val hookHandleVariable = newLocal(ENTITY_TYPE)
                invokeVirtual("getEntity", hookType, NMS_INSENTIENT_ENTITY_TYPE)
                invokeInterface("getHandle", NMS_INSENTIENT_ENTITY_TYPE, OBJECT_TYPE)
                visitTypeInsn(CHECKCAST, ENTITY_TYPE.internalName)
                visitInsn(DUP)
                visitVarInsn(ASTORE, hookHandleVariable) // Store for use in the error message
                // Check for reference equality to this object, since we should be _exactly_ the same
                loadThis()
                runIf(IF_ACMPNE) {
                    loadThis()
                    visitVarInsn(ALOAD, hookHandleVariable)
                    throwFormattedException(
                            IllegalStateException::class.java,
                            "Hook's entity doesn't match this {}: {}",
                            2
                    )
                }
            })
        }

    // Obfuscated method names
    val entityTickMethodName = Versioning.NMS_VERSION.getObfuscatedMethod("ENTITY_TICK_METHOD")
    val entityMoveMethodName = Versioning.NMS_VERSION.getObfuscatedMethod("ENTITY_MOVE_METHOD")
    /**
     * Before 1.12, the entity move method accepted two floats for both sideways and forwards direction.
     * After 1.12, it also accepts an additional float for up/down movement, giving it three paramteers in total.
     */
    val entityMoveMethodParameters: Array<Type>
        get() {
            val amount = if (Versioning.NMS_VERSION >= NmsVersion.v1_12_R1) 3 else 2
            return Array(amount) { FLOAT_TYPE }
        }
    val onStepMethodName = Versioning.NMS_VERSION.getObfuscatedMethod("ON_STEP_METHOD")
    val onInteractMethodName = Versioning.NMS_VERSION.getObfuscatedMethod("ON_INTERACT_METHOD")
    val proceduralAIMethodName = Versioning.NMS_VERSION.getObfuscatedMethod("ENTITY_PROCEDURAL_AI_METHOD")
}

// Type constants
val NMS_INSENTIENT_ENTITY_TYPE = NMSInsentientEntity::class.asmType
val BLOCK_POSITION_TYPE = MinecraftReflection.getNmsClass("BlockPosition").asmType
val BLOCK_TYPE = MinecraftReflection.getNmsClass("Block").asmType
val ENTITY_TYPE = MinecraftReflection.getNmsClass("Entity").asmType
val ENTITY_HUMAN_TYPE = MinecraftReflection.getNmsClass("EntityHuman").asmType
val CRAFT_HUMAN_ENTITY_TYPE = MinecraftReflection.getObcClass("entity.CraftHumanEntity").asmType
val WORLD_TYPE = MinecraftReflection.getNmsClass("World").asmType
val ENTITY_LIVING_TYPE = MinecraftReflection.getNmsClass("EntityLiving").asmType
