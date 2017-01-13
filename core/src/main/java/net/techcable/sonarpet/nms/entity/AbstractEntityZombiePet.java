package net.techcable.sonarpet.nms.entity;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.plugin.EchoPet;

import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractEntityZombiePet extends EntityInsentientPet {
    protected NMSInsentientEntity entity;

    protected AbstractEntityZombiePet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }

    @Override
    public void initiateEntityPet() {
        super.initiateEntityPet();
        new BukkitRunnable() {
            @Override
            public void run() {
                getBukkitEntity().getEquipment().setItemInMainHand(new ItemStack(getInitialItemInHand()));
            }
        }.runTaskLater(EchoPet.getPlugin(), 5L);
    }

    protected abstract Material getInitialItemInHand();

    public void setBaby(boolean flag) {
        getBukkitEntity().setBaby(flag);
    }

    public boolean isBaby() {
        return getBukkitEntity().isBaby();
    }

    public SizeCategory getSizeCategory() {
        if (getBukkitEntity().isBaby()) {
            return SizeCategory.TINY;
        } else {
            return SizeCategory.REGULAR;
        }
    }

    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }

    @Override
    public Zombie getBukkitEntity() {
        return (Zombie) super.getBukkitEntity();
    }
}
