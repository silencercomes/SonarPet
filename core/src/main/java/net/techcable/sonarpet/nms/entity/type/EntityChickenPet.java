package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityChickenPet;

import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.entity.EntityAgeablePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

@EntityPetType(petType = PetType.CHICKEN)
public class EntityChickenPet extends EntityAgeablePet implements IEntityChickenPet {
    protected EntityChickenPet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }

    @Override
    public void makeStepSound() {
        getEntity().playSound(SafeSound.CHICKEN_STEP, 0.15F, 1.0F);
    }
}
