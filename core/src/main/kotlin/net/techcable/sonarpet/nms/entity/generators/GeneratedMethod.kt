package net.techcable.sonarpet.nms.entity.generators

import net.techcable.sonarpet.utils.asmType
import net.techcable.sonarpet.utils.bytecode.MethodGenerator
import net.techcable.sonarpet.utils.isWrapperType
import net.techcable.sonarpet.utils.primitiveWrapperType
import net.techcable.sonarpet.utils.toImmutableList
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.Type.*
import java.lang.invoke.MethodHandle
import java.util.function.Consumer
import kotlin.reflect.KClass

class GeneratedMethod(
        val name: String,
        val returnType: Type = VOID_TYPE,
        parameterTypes: List<Type> = listOf(),
        val access: Int = ACC_PUBLIC,
        val invokeCheckSanity: Boolean = true, // Almost everyone should invoke checkSanity
        val generator: MethodGenerator.(GenerationState) -> Unit
) {
    val parameterTypes = parameterTypes.toImmutableList() // Paranoid
    fun generate(state: GenerationState) {
        state.generator.generateMethod(
                Consumer { methodGenerator ->
                    if (invokeCheckSanity) with(state) {
                        methodGenerator.generateCheckSanity()
                    }
                    generator(methodGenerator, state)
                },
                access,
                name,
                returnType,
                *parameterTypes.toTypedArray()
        )
    }

    constructor(
            name: String,
            returnType: KClass<*>? = null, // null -> void
            parameterTypes: List<KClass<*>> = listOf(),
            access: Int = ACC_PUBLIC,
            generator: MethodGenerator.(GenerationState) -> Unit
    ) : this(
            name = name,
            returnType = returnType?.asmType ?: VOID_TYPE,
            parameterTypes = parameterTypes.map(KClass<*>::asmType),
            access = access,
            generator = generator
    )

    companion object {
        fun createGetter(
                fieldName: String,
                fieldType: Type,
                returnType: Type = fieldType,
                methodName: String = "get${fieldName.firstToUpper()}",
                access: Int = ACC_PUBLIC
        ): GeneratedMethod {
            return GeneratedMethod(
                    name = methodName,
                    returnType = returnType,
                    access = access
            ) { state ->
                loadThis()
                getField(state.currentType, fieldName, fieldType)
            }
        }
        fun noOp(
                methodName: String,
                returnType: Type = VOID_TYPE,
                access: Int = ACC_PUBLIC,
                parameterTypes: List<Type> = listOf()
        ): GeneratedMethod {
            return GeneratedMethod(
                    name = methodName,
                    returnType = returnType,
                    parameterTypes = parameterTypes,
                    access = access,
                    generator = {}
            )
        }
        fun returnConstant(
                methodName: String,
                constant: Any,
                access: Int = ACC_PUBLIC,
                parameterTypes: List<Type> = listOf()
        ): GeneratedMethod {
            val constantType: Type = when (constant) {
                is Boolean -> BOOLEAN_TYPE
                is Number -> {
                    require(constant.javaClass.isWrapperType) { "Constant number isn't a primitive wrapper type: $constant" }
                    constant.javaClass.primitiveWrapperType.asmType
                }
                is Class<*> -> Class::class.asmType
                is Type -> if (constant.sort == METHOD) MethodHandle::class.asmType else Class::class.asmType
                is Handle -> MethodHandle::class.asmType
                else -> throw IllegalArgumentException("Invalid constant: $constant")
            }
            return GeneratedMethod(
                    name = methodName,
                    returnType = constantType,
                    access = access,
                    parameterTypes = parameterTypes
            ) {
                when (constant) {
                // Booleans are represented on the stack as ints -_-
                    is Boolean -> visitInsn(if (constant) ICONST_1 else ICONST_0)
                    is Class<*> -> visitLdcInsn(constant.asmType)
                    else -> visitLdcInsn(constant)
                }
            }
        }
    }
}

// Utils
private fun String.firstToUpper() = this[0].toUpperCase() + substring(1)
