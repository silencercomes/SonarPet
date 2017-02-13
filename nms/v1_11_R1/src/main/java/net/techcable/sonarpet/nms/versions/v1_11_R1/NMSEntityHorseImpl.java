package net.techcable.sonarpet.nms.versions.v1_11_R1;

import com.dsh105.echopet.compat.api.entity.HorseType;

import net.minecraft.server.v1_11_R1.EntityHorse;
import net.minecraft.server.v1_11_R1.EntityHorseAbstract;
import net.techcable.sonarpet.nms.NMSEntityHorse;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Horse;

public class NMSEntityHorseImpl extends NMSEntityInsentientImpl implements NMSEntityHorse {
    public NMSEntityHorseImpl(EntityHorseAbstract handle) {
        super(handle);
    }

    // Deobfuscated methods

    @Override
    public void setRearing(boolean b) {
        getHandle().setStanding(b);
    }

    @Override
    public void setStyle(Horse.Style bukkitStyle) {
        ((Horse) getBukkitEntity()).setStyle(bukkitStyle);
    }

    @Override
    public void setColor(Horse.Color color) {
        ((Horse) getHandle()).setColor(color);
    }

    @Override
    public HorseType getHorseType() {
        return HorseType.getForBukkitVariant(getBukkitEntity().getVariant());
    }

    @Override
    public void setCarryingChest(boolean flag) {
        ((ChestedHorse) getBukkitEntity()).setCarryingChest(flag);
    }

    @Override
    public EntityHorseAbstract getHandle() {
        return (EntityHorseAbstract) super.getHandle();
    }

    @Override
    public AbstractHorse getBukkitEntity() {
        return (Horse) super.getBukkitEntity();
    }
}
