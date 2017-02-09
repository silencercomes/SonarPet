package net.techcable.sonarpet.utils.reflection;

import lombok.*;

import java.lang.invoke.MethodHandle;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Primitives;

import net.techcable.pineapple.reflection.PineappleField;
import net.techcable.pineapple.reflection.Reflection;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static com.google.common.base.Preconditions.checkArgument;
import static net.techcable.sonarpet.utils.Versioning.NMS_PACKAGE;
import static net.techcable.sonarpet.utils.Versioning.OBC_PACKAGE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MinecraftReflection {

    public static Class<?> getObcClass(String id) {
        return Reflection.getClass(OBC_PACKAGE + "." + id);
    }

    public static Class<?> getNmsClass(String id) {
        return Reflection.getClass(NMS_PACKAGE + "." + id);
    }

    // Constant NMS types
    public static final Class<?> ENTITY_CLASS = getNmsClass("Entity");
    public static final Class<?> ENTITY_PLAYER_CLASS = getNmsClass("EntityPlayer");
    public static final Class<?> PACKET_CLASS = getNmsClass("Packet");
    public static final Class<?> PLAYER_CONNECTION_CLASS = getNmsClass("PlayerConnection");
    // Constant OBC types
    public static final Class<?> CRAFT_ENTITY_CLASS = getObcClass("entity.CraftEntity");

    //
    // Reflection wrappers
    //
    private static final MethodHandle GET_HANDLE_METHOD = Reflection.getMethod(getObcClass("entity.CraftEntity"), "getHandle");
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
