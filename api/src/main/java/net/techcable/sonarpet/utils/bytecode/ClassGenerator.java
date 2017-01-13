package net.techcable.sonarpet.utils.bytecode;

import java.io.PrintWriter;
import java.util.function.Consumer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class ClassGenerator extends ClassVisitor {
    private Type currentType;
    public ClassGenerator(ClassVisitor visitor) {
        super(ASM5, visitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        currentType = Type.getObjectType(name);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public void generateField(int access, String name, Type fieldType) {
        super.visitField(access, name, fieldType.getDescriptor(), null, null);
    }

    public void generateConstructor(
            Consumer<MethodGenerator> generator,
            int access,
            Type... parameterTypes
    ) {
        generateMethod(generator, access, "<init>", Type.VOID_TYPE, parameterTypes);
    }

    private static final boolean ASSERTIONS_ENABLED = ClassGenerator.class.desiredAssertionStatus();
    public void generateMethod(
            Consumer<MethodGenerator> generator,
            int access,
            String name,
            Type returnType,
            Type... parameterTypes
    ) {
        String desc = Type.getMethodDescriptor(returnType, parameterTypes);
        Textifier textifier = new Textifier();
        MethodGenerator generatorInstance = new MethodGenerator(
                super.visitMethod(access, name, desc, null, null),
                access,
                name,
                desc
        );
        generator.accept(generatorInstance);
        generatorInstance.returnValue();
        generatorInstance.visitMaxs(0, 0); // trigger automatic computing
        generatorInstance.visitEnd();
    }
}
