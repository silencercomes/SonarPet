package net.techcable.sonarpet.utils.reflection;

import lombok.*;

import java.lang.invoke.MethodHandle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.techcable.pineapple.reflection.PineappleField;
import net.techcable.pineapple.reflection.Reflection;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static com.google.common.base.Preconditions.*;
import static net.techcable.sonarpet.utils.Versioning.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MinecraftReflection {

    @Nullable
    public static Class<?> getObcClass(String id) {
        return Reflection.getClass(OBC_PACKAGE + "." + id);
    }

    @Nonnull
    public static Class<?> findObcClass(String id) {
        Class<?> result = getObcClass(id);
        if (result == null) throw new IllegalArgumentException("Couldn't find OBC class " + id);
        return result;
    }

    @Nullable
    public static Class<?> getNmsClass(String id) {
        return Reflection.getClass(NMS_PACKAGE + "." + id);
    }

    @Nonnull
    public static Class<?> findNmsClass(String id) {
        Class<?> result = getNmsClass(id);
        if (result == null) throw new IllegalArgumentException("Couldn't find NMS class " + id);
        return result;
    }

    // Constant NMS types
    public static final Class<?> ENTITY_CLASS = findNmsClass("Entity");
    public static final Class<?> ENTITY_PLAYER_CLASS = findNmsClass("EntityPlayer");
    public static final Class<?> PACKET_CLASS = findNmsClass("Packet");
    public static final Class<?> PLAYER_CONNECTION_CLASS = findNmsClass("PlayerConnection");
    // Constant OBC types
    public static final Class<?> CRAFT_ENTITY_CLASS = getObcClass("entity.CraftEntity");

    //
    // Reflection wrappers
    //
    private static final MethodHandle GET_HANDLE_METHOD = Reflection.getMethod(findObcClass("entity.CraftEntity"), "getHandle");

    @SneakyThrows
    public static Object getHandle(Entity entity) {
        return GET_HANDLE_METHOD.invoke(entity);
    }

    private static final PineappleField PLAYER_CONNECTION_FIELD = PineappleField.create(ENTITY_PLAYER_CLASS, "playerConnection", PLAYER_CONNECTION_CLASS);
    private static final MethodHandle SEND_PACKET_METHOD = Reflection.getMethodWithReturnType(PLAYER_CONNECTION_CLASS, "sendPacket", void.class, PACKET_CLASS);

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static void sendPacket(Player player, Object packet) {
        checkArgument(PACKET_CLASS.isInstance(packet), "Invalid packet: %s", packet);
        Object playerConnection = PLAYER_CONNECTION_FIELD.get(getHandle(player));
        SEND_PACKET_METHOD.invoke(playerConnection, packet);
    }
}
