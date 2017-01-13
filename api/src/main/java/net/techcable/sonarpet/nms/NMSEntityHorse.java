package net.techcable.sonarpet.nms;

import com.dsh105.echopet.compat.api.entity.HorseType;

import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;

/**
 * Version-dependent methods for a horse entity.
 * <p>
 * Most of these methods should delegate to the appropriate bukkit API calls.
 * However these bukkit api calls are now version-dependent, so they need to be moved out of the core module/
 */
public interface NMSEntityHorse extends NMSInsentientEntity {
    /**
     * Set if the horse is rearing.
     * <p>The set rearing method should be the metadata flag with id 64</p>
     */
    void setRearing(boolean b);

    void setColor(Horse.Color color);

    void setStyle(Horse.Style bukkitStyle);

    HorseType getHorseType();

    /**
     * Set the horse's type, if it's possible on the current version.
     * <text>
     * #if NMS_VERSION >= v1_11_R1
     * throw new UnsupportedOperationException()
     * #elif NMS_VERSION >= v1_9_R1
     * import net.minecraft.server.NMS_VERSION_STRING.EnumHorseType;
     * entity.setType(EnumHorseType.values()[((Horse.Variant) variant).ordinal()])
     * #else
     * entity.setType(((Horse.Variant) variant).ordinal())
     * #endif
     * </text>
     * @param t the new type
     * @throws UnsupportedOperationException if unsupported
     * @deprecated unsafe
     */
    @Deprecated
    default void setHorseType(HorseType t) {
        throw new UnsupportedOperationException("Unable to change horse type on " + Versioning.NMS_VERSION);
    }

    void setCarryingChest(boolean flag);
}
