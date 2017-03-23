package net.techcable.sonarpet.utils.bytecode;

import java.lang.invoke.MethodHandle;
import java.util.Collections;
import javax.annotation.Nullable;

import net.techcable.pineapple.SimpleFormatter;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import static org.objectweb.asm.Opcodes.*;

public class MethodGenerator extends LocalVariablesSorter {
    private final int access;
    private final String name;
    private final Type methodType;
    public MethodGenerator(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(ASM5, access, desc, methodVisitor);
        this.access = access;
        this.name = name;
        this.methodType = Type.getMethodType(desc);
    }

    public void loadThis() {
        visitVarInsn(ALOAD, 0);
    }

    public void loadArg(int index) {
        int offset = (access & ACC_STATIC) != 0 ? 0 : 1;
        int opcode = methodType.getArgumentTypes()[index].getOpcode(ILOAD);
        visitVarInsn(opcode, offset + index);
    }

    public void loadArgs() {
        for (int i = 0; i < methodType.getArgumentTypes().length; i++) {
            loadArg(i);
        }
    }

    public void getField(Type ownerType, String name, Type fieldType) {
        visitFieldInsn(GETFIELD, ownerType.getInternalName(), name, fieldType.getDescriptor());
    }

    public void putField(Type ownerType, String name, Type fieldType) {
        visitFieldInsn(PUTFIELD, ownerType.getInternalName(), name, fieldType.getDescriptor());
    }

    public void invokeSpecial(String name, Type ownerType, Type returnType, Type... parameterTypes) {
        super.visitMethodInsn(
                INVOKESPECIAL,
                ownerType.getInternalName(),
                name,
                Type.getMethodDescriptor(returnType, parameterTypes),
                false
        );
    }

    public void invokeStatic(String name, Type ownerType, Type returnType, Type... parameterTypes) {
        super.visitMethodInsn(
                INVOKESTATIC,
                ownerType.getInternalName(),
                name,
                Type.getMethodDescriptor(returnType, parameterTypes),
                false
        );
    }

    public void invokeVirtual(String name, Type ownerType, Type returnType, Type... parameterTypes) {
        super.visitMethodInsn(
                INVOKEVIRTUAL,
                ownerType.getInternalName(),
                name,
                Type.getMethodDescriptor(returnType, parameterTypes),
                false
        );
    }

    public void invokeInterface(String name, Type ownerType, Type returnType, Type... parameterTypes) {
        super.visitMethodInsn(
                INVOKEINTERFACE,
                ownerType.getInternalName(),
                name,
                Type.getMethodDescriptor(returnType, parameterTypes),
                true
        );
    }

    public void returnValue() {
        visitInsn(methodType.getReturnType().getOpcode(IRETURN));
    }

    public void arrayLiteral(Type arrayType, int size) {
        // Allocate temporary variables to store the arguments
        int[] variables = new int[size];
        for (int i = 0; i < size; i++) {
            variables[i] = newLocal(arrayType);
        }
        // Pop the arguments of the stack and into the variables
        for (int i = 0; i < size; i++) {
            visitVarInsn(arrayType.getOpcode(ISTORE), variables[i]);
        }
        visitLdcInsn(size);
        visitTypeInsn(ANEWARRAY, arrayType.getInternalName());
        // Fill the (currently empty) array
        for (int i = 0; i < size; i++) {
            visitInsn(DUP); // Duplicate the array to maintain it on top of the stack
            visitLdcInsn(i);
            visitVarInsn(arrayType.getOpcode(ILOAD), variables[i]);
            visitInsn(arrayType.getOpcode(IASTORE));
        }
    }

    public void formatString(String format, int numVars) {
        // Verify the format string at compile time
        SimpleFormatter.format(format, Collections.nCopies(numVars, "").toArray());
        if (numVars == 0) {
            visitLdcInsn(format);
            return;
        }
        arrayLiteral(OBJECT_TYPE, numVars);
        visitLdcInsn(format);
        visitInsn(SWAP); // array, format -> format, array
        invokeStatic(
                "format",
                SIMPLE_FORMATTER_TYPE,
                STRING_TYPE,
                STRING_TYPE,
                OBJECT_ARRAY_TYPE
        );
    }

    public void throwFormattedException(Class<? extends Throwable> exceptionClass, String format, int numVars) {
        throwFormattedException(Type.getType(exceptionClass), format, numVars);
    }

    public void throwFormattedException(Type exceptionType, String format, int numVars) {
        formatString(format, numVars);
        visitTypeInsn(NEW, exceptionType.getInternalName());
        visitInsn(DUP_X1); // message, exception -> exception, message, exception
        visitInsn(SWAP); // message, exception -> exception, message
        invokeSpecial("<init>", exceptionType, Type.VOID_TYPE, STRING_TYPE);
        visitInsn(ATHROW);
    }

    public void runIfElse(int conditionCode, @Nullable Runnable thenCode, @Nullable Runnable elseCode) {
        Label finishBranching = new Label();
        Label codeIfTrue = thenCode != null ? new Label() : finishBranching;
        visitJumpInsn(conditionCode, codeIfTrue);
        // When we fallthrough, emit code for else, since the condition is false
        if (elseCode != null) elseCode.run();
        // After this, we either fallthrough to the end or we jump
        if (thenCode != null) {
            visitJumpInsn(GOTO, finishBranching); // Jump the else code to the end
            visitLabel(codeIfTrue);
            thenCode.run();
        }
        visitLabel(finishBranching);
    }

    public void runIfNot(int conditionCode, Runnable conditionalCode) {
        runIfElse(conditionCode, null, conditionalCode);
    }

    public void runIf(int conditionCode, Runnable conditionalCode) {
        runIfElse(conditionCode, conditionalCode, null);
    }

    //
    // Type constants
    //
    public static final Type METHOD_HANDLE_TYPE = Type.getType(MethodHandle.class);
    public static final Type STRING_TYPE = Type.getType(String.class);
    public static final Type SIMPLE_FORMATTER_TYPE = Type.getType(SimpleFormatter.class);
    public static final Type OBJECT_TYPE = Type.getType(Object.class);
    public static final Type OBJECT_ARRAY_TYPE = Type.getType(Object[].class);
}
