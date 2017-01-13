package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityCowPet;

import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.entity.EntityAgeablePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

@EntityPetType(petType = PetType.COW)
public class EntityCowPet extends EntityAgeablePet implements IEntityCowPet {
    private final NMSInsentientEntity entity;

    protected EntityCowPet(IPet pet, NMSInsentientEntity entity) {
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
