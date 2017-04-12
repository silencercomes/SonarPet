package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySilverfishPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;

@EntityHook(EntityHookType.SILVERFISH)
public class EntitySilverfishPet extends EntityInsentientPet implements IEntitySilverfishPet {
    protected EntitySilverfishPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
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
