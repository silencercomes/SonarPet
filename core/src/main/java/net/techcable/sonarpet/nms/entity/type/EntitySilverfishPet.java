package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySilverfishPet;

import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

@EntityPetType(petType = PetType.SILVERFISH)
public class EntitySilverfishPet extends EntityInsentientPet implements IEntitySilverfishPet {
    protected EntitySilverfishPet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }

    @Override
    public void makeStepSound() {
        getEntity().playSound(SafeSound.SILVERFISH_STEP, 0.15F, 1.0F);
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.TINY;
    }
}
