package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityCaveSpiderPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;

@EntityHook(EntityHookType.CAVE_SPIDER)
public class EntityCaveSpiderPet extends EntityInsentientPet implements IEntityCaveSpiderPet {
    protected EntityCaveSpiderPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public void makeStepSound() {
        getEntity().playSound(SafeSound.SPIDER_STEP, 0.15F, 1.0F);
    }
}
