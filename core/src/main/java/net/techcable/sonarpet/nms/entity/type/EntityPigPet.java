package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityPigPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityAgeablePet;

import org.bukkit.entity.Pig;

@EntityHook(EntityHookType.PIG)
public class EntityPigPet extends EntityAgeablePet implements IEntityPigPet {
    protected EntityPigPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
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
