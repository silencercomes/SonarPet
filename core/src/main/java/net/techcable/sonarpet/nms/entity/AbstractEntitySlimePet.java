package net.techcable.sonarpet.nms.entity;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetData;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySlimePet;
import com.dsh105.echopet.compat.api.util.Perm;

import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.entity.Slime;

public abstract class AbstractEntitySlimePet extends EntityInsentientPet implements IEntitySlimePet {

    protected AbstractEntitySlimePet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public Slime getBukkitEntity() {
        return (Slime) super.getBukkitEntity();
    }

    @Override
    public void initiateEntityPet() {
        super.initiateEntityPet();
        if (!Perm.hasDataPerm(getPet().getOwner(), false, getPet().getPetType(), PetData.MEDIUM, false)) {
            if (!Perm.hasDataPerm(getPet().getOwner(), false, getPet().getPetType(), PetData.SMALL, false)) {
                this.setSize(4);
            } else {
                this.setSize(1);
            }
        } else {
            this.setSize(2);
        }
    }

    @Override
    public void setSize(int i) {
        getBukkitEntity().setSize(i);
    }

    public int getSize() {
        return getBukkitEntity().getSize();
    }

    @Override
    public SizeCategory getSizeCategory() {
        switch (getSize()) {
            case 1:
                return SizeCategory.TINY;
            case 4:
                return SizeCategory.LARGE;
            default:
                return SizeCategory.REGULAR;
        }
    }

}
