package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetData;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityWolfPet;

import net.techcable.sonarpet.nms.entity.EntityAgeablePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.DyeColor;
import org.bukkit.entity.Wolf;

@EntityPetType(petType = PetType.WOLF)
public class EntityWolfPet extends EntityAgeablePet implements IEntityWolfPet {
    private final NMSInsentientEntity entity;

    protected EntityWolfPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }

    @Override
    public void initiateEntityPet() {
        super.initiateEntityPet();
        setTamed(true); // Tame
    }

    @Override
    public void setTamed(boolean flag) {
        getBukkitEntity().setTamed(flag);

        if (!flag) {
            getPet().getPetData().remove(PetData.TAMED);
        } else if (!getPet().getPetData().contains(PetData.TAMED)) {
            this.getPet().getPetData().add(PetData.TAMED);
        }

        if (isAngry() && flag) {
            setAngry(false);
        }
    }

    @Override
    public void setAngry(boolean flag) {
        if (flag) {
            if (!getPet().getPetData().contains(PetData.ANGRY)) {
                this.getPet().getPetData().add(PetData.ANGRY);
            }
            if (getBukkitEntity().isTamed()) {
                setTamed(false);
            }
        } else {
            getPet().getPetData().remove(PetData.ANGRY);
        }

        getBukkitEntity().setAngry(flag);
    }

    public boolean isAngry() {
        return getBukkitEntity().isAngry();
    }

    @Override
    public void setCollarColor(DyeColor dc) {
        getBukkitEntity().setCollarColor(dc);
    }

    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }

    @Override
    public Wolf getBukkitEntity() {
        return (Wolf) super.getBukkitEntity();
    }
}
