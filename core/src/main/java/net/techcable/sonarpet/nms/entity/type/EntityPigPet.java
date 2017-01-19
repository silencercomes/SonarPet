package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityPigPet;

import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.entity.EntityAgeablePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.entity.Pig;

@EntityPetType(petType = PetType.PIG)
public class EntityPigPet extends EntityAgeablePet implements IEntityPigPet {
    protected EntityPigPet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }

    @Override
    public void setSaddled(boolean flag) {
        ((Pig) getBukkitEntity()).setSaddle(flag);
    }

    @Override
    public void makeStepSound() {
        getEntity().playSound(SafeSound.PIG_STEP, 0.15F, 1.0F);
    }
}
