package net.techcable.sonarpet.nms.versions.v1_10_R1;

import com.dsh105.echopet.compat.api.entity.HorseType;
import com.dsh105.echopet.compat.api.entity.HorseTypeKt;

import net.minecraft.server.v1_10_R1.EntityHorse;
import net.techcable.sonarpet.nms.NMSEntityHorse;

import org.bukkit.entity.Horse;

public class NMSEntityHorseImpl extends NMSEntityInsentientImpl implements NMSEntityHorse {
    public NMSEntityHorseImpl(EntityHorse handle) {
        super(handle);
    }

    //
    // !!!!! Highly version-dependent !!!!!
    // Check these every minor update!
    //

    @Override
    public void setSaddled(boolean saddled) {
        getHandle().u(saddled); // EntityHorse.setHorseSaddled
    }

    @Override
    public boolean isSaddled() {
        return getHandle().dz(); // EntityHorse.isHorseSaddled
    }

    //
    // Breakage likely, check for bugs here
    //

    @Override
    public void setRearing(boolean b) {
        // metadata flag with id 64
        getHandle().v(b);
    }

    // Deobfuscated methods

    @Override
    public void setStyle(Horse.Style bukkitStyle) {
        getBukkitEntity().setStyle(bukkitStyle);
    }

    @Override
    public void setHorseType(HorseType t) {
        getBukkitEntity().setVariant(t.getBukkitVariant());
    }

    @Override
    public void setColor(Horse.Color color) {
        getBukkitEntity().setColor(color);
    }

    @Override
    public HorseType getHorseType() {
        return HorseTypeKt.getSonarType(getBukkitEntity().getVariant());
    }

    @Override
    public void setCarryingChest(boolean flag) {
        getBukkitEntity().setCarryingChest(flag);
    }

    @Override
    public EntityHorse getHandle() {
        return (EntityHorse) super.getHandle();
    }

    @Override
    public Horse getBukkitEntity() {
        return (Horse) super.getBukkitEntity();
    }
}
