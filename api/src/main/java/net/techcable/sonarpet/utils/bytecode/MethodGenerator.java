package net.techcable.sonarpet.utils.bytecode;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import static org.objectweb.asm.Opcodes.*;

public class MethodGenerator extends MethodVisitor {
    private final int access;
    private final String name;
    private final Type methodType;
    public MethodGenerator(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(ASM5, methodVisitor);
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

    public void invokeSpecial(String name, Type ownerType, Type returnType, Type... parameterTypes) {
        super.visitMethodInsn(
                INVOKESPECIAL,
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

    public void returnValue() {
        visitInsn(methodType.getReturnType().getOpcode(IRETURN));
    }
}
