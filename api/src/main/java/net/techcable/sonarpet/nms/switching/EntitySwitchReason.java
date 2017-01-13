package net.techcable.sonarpet.nms.switching;

import lombok.*;

import com.dsh105.echopet.compat.api.entity.HorseType;

import net.techcable.sonarpet.utils.NmsVersion;

@Getter
public enum EntitySwitchReason {
    GUARDIAN_SWITCH(NmsVersion.v1_11_R1, GuardianType.class),
    HORSE_SWITCH(NmsVersion.v1_11_R1, HorseType.class),
    ZOMBIE_SWITCH(NmsVersion.v1_11_R1, ZombieType.class),
    SKELETON_SWITCH(NmsVersion.v1_11_R1, SkeletonType.class);

    /**
     * The version when we need to start actually switching the entity.
     */
    private final NmsVersion version;
    private final Class<? extends Enum> switchTypeClass;

    EntitySwitchReason(NmsVersion version, Class<? extends Enum> switchTypeClass) {
        this.version = version;
        this.switchTypeClass = switchTypeClass;
    }
}
