package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityChickenPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityAgeablePet;

@EntityHook(EntityHookType.CHICKEN)
public class EntityChickenPet extends EntityAgeablePet implements IEntityChickenPet {
    protected EntityChickenPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public void makeStepSound() {
        getEntity().playSound(SafeSound.CHICKEN_STEP, 0.15F, 1.0F);
    }
}
