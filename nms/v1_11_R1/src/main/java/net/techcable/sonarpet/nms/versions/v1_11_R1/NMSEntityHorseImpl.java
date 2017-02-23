package net.techcable.sonarpet.nms.versions.v1_11_R1;

import com.dsh105.echopet.compat.api.entity.HorseType;

import net.minecraft.server.v1_11_R1.EntityHorseAbstract;
import net.techcable.sonarpet.nms.NMSEntityHorse;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Horse;

public class NMSEntityHorseImpl extends NMSEntityInsentientImpl implements NMSEntityHorse {
    public NMSEntityHorseImpl(EntityHorseAbstract handle) {
        super(handle);
    }

    //
    // !!!!! Highly version-dependent !!!!!
    // Check these every minor update!
    //

    @Override
    public void setSaddled(boolean saddled) {
        getHandle().t(saddled); // AbstractHorse.setHorseSaddled
    }

    @Override
    public boolean isSaddled() {
        return getHandle().dB(); // AbstractHorse.isHorseSaddled
    }

    // Deobfuscated methods

    @Override
    public void setRearing(boolean b) {
        getHandle().setStanding(b);
    }

    @Override
    public void setStyle(Horse.Style bukkitStyle) {
        if (getBukkitEntity() instanceof Horse) {
            ((Horse) getBukkitEntity()).setStyle(bukkitStyle);
        }
    }

    @Override
    public void setColor(Horse.Color color) {
        if (getBukkitEntity() instanceof Horse) {
            ((Horse) getBukkitEntity()).setColor(color);
        }
    }

    @Override
    public HorseType getHorseType() {
        return HorseType.getForBukkitVariant(getBukkitEntity().getVariant());
    }

    @Override
    public void setCarryingChest(boolean flag) {
        if (getBukkitEntity() instanceof ChestedHorse) {
            ((ChestedHorse) getBukkitEntity()).setCarryingChest(flag);
        }
    }

    @Override
    public EntityHorseAbstract getHandle() {
        return (EntityHorseAbstract) super.getHandle();
    }

    @Override
    public AbstractHorse getBukkitEntity() {
        return (AbstractHorse) super.getBukkitEntity();
    }
}
