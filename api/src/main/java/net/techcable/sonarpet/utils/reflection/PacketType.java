package net.techcable.sonarpet.utils.reflection;

import lombok.*;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import net.techcable.pineapple.SimpleFormatter;
import net.techcable.pineapple.reflection.PineappleField;
import net.techcable.pineapple.reflection.PrimitiveType;
import net.techcable.pineapple.reflection.Reflection;

import static com.google.common.base.Preconditions.*;
import static net.techcable.pineapple.SimpleFormatter.format;

public final class PacketType {
    private final Class<?> packetClass;
    private final MethodHandle constructor;
    private final ImmutableList<PineappleField<Object, Object>> referenceFields;
    private final ImmutableList<PineappleField<Object, Float>> floatFields;
    private final ImmutableList<PineappleField<Object, Integer>> intFields;
    private final ImmutableList<PineappleField<Object, Boolean>> booleanFields;
    @SuppressWarnings("unchecked")
    private PacketType(Class<?> packetClass) {
        this.packetClass = checkNotNull(packetClass);
        checkArgument(MinecraftReflection.PACKET_CLASS.isAssignableFrom(packetClass));
        this.constructor = Reflection.getConstructor(packetClass);
        ImmutableList.Builder<PineappleField<Object, Object>> referenceFields = ImmutableList.builder();
        ImmutableList.Builder<PineappleField<Object, Float>> floatFields = ImmutableList.builder();
        ImmutableList.Builder<PineappleField<Object, Integer>> intFields = ImmutableList.builder();
        ImmutableList.Builder<PineappleField<Object, Boolean>> booleanFields = ImmutableList.builder();
        for (Field field : packetClass.getDeclaredFields()) {
            PineappleField pineappleField = PineappleField.fromField(field);
            PrimitiveType primitiveType = PrimitiveType.fromClass(field.getType());
            if (primitiveType != null) {
                switch (primitiveType) {
                    case FLOAT:
                        floatFields.add(pineappleField);
                        break;
                    case INT:
                        intFields.add(pineappleField);
                        break;
                    case BOOLEAN:
                        booleanFields.add(pineappleField);
                        break;
                }
            } else {
                referenceFields.add(pineappleField);
            }
        }
        this.referenceFields = referenceFields.build();
        this.floatFields = floatFields.build();
        this.intFields = intFields.build();
        this.booleanFields = booleanFields.build();
    }

    @SneakyThrows
    public Object newPacket() {
        return constructor.invoke();
    }

    public void putObject(Object packet, int index, Object value) {
        if (index < referenceFields.size()) {
            referenceFields.get(index).put(packet, value);
        } else {
            throw new IllegalArgumentException("Invalid object index " + index + " for " + packetClass);
        }
    }

    public void putFloat(Object packet, int index, float value) {
        if (index < floatFields.size()) {
            floatFields.get(index).putBoxed(packet, value);
        } else {
            throw new IllegalArgumentException("Invalid float index " + index + " for " + this);
        }
    }

    public void putInt(Object packet, int index, int value) {
        if (index < intFields.size()) {
            intFields.get(index).putBoxed(packet, value);
        } else {
            throw new IllegalArgumentException("Invalid int index " + index + " for " + this);
        }
    }

    public void putBoolean(Object packet, int index, boolean value) {
        if (index < booleanFields.size()) {
            booleanFields.get(index).putBoxed(packet, value);
        } else {
            throw new IllegalArgumentException("Invalid boolean index " + index + " for " + this);
        }
    }

    private static final ClassValue<PacketType> PACKET_TYPES = new ClassValue<PacketType>() {
        @Override
        protected PacketType computeValue(Class<?> type) {
            return new PacketType(type);
        }
    };
    public static PacketType forName(String name) {
        return forClass(MinecraftReflection.getNmsClass("Packet" + name));
    }
    public static PacketType forClass(Class<?> type) {
        checkArgument(MinecraftReflection.PACKET_CLASS.isAssignableFrom(type), "Type isn't a packet type: %s", type);
        return PACKET_TYPES.get(type);  
    }

    @Override
    public String toString() {
        return "PacketType(" + packetClass.getSimpleName() + ")";
    }
}
