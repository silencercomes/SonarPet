package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityMushroomCowPet;

import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.entity.EntityAgeablePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

@EntityPetType(petType = PetType.MUSHROOMCOW)
public class EntityMushroomCowPet extends EntityAgeablePet implements IEntityMushroomCowPet {
    private final NMSInsentientEntity entity;

    protected EntityMushroomCowPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }

    @Override
    public void makeStepSound() {
        getEntity().playSound(SafeSound.COW_STEP, 0.15F, 1.0F);
    }

    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }
}
