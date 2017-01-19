package net.techcable.sonarpet.nms.entity;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.dsh105.echopet.compat.api.entity.IEntityAgeablePet;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.SizeCategory;

import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.entity.Ageable;

public abstract class EntityAgeablePet extends EntityInsentientPet implements IEntityAgeablePet{

    public EntityAgeablePet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }

    @Override
    public Ageable getBukkitEntity() {
        return (Ageable) super.getBukkitEntity();
    }

    public int getAge() {
        return getBukkitEntity().getAge();
    }

    public void setAge(int age) {
        getBukkitEntity().setAge(age);
    }

    public boolean isAgeLocked() {
        return getBukkitEntity().getAgeLock();
    }

    @Override
    public void setBaby(boolean flag) {
        if (flag) {
            getBukkitEntity().setBaby();
        } else {
            getBukkitEntity().setAdult();
        }
    }

    public boolean isBaby() {
        return !getBukkitEntity().isAdult();
    }

    @Override
    public SizeCategory getSizeCategory() {
        if (this.isBaby()) {
            return SizeCategory.TINY;
        } else {
            return SizeCategory.REGULAR;
        }
    }

    @Override
    public void initiateEntityPet() {
        super.initiateEntityPet();
        getBukkitEntity().setAgeLock(true);
    }
}
