package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityBlazePet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;

import org.bukkit.entity.Blaze;

@EntityHook(EntityHookType.BLAZE)
public class EntityBlazePet extends EntityInsentientPet implements IEntityBlazePet {
    protected EntityBlazePet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public void setOnFire(boolean flag) {
        getBukkitEntity().setFireTicks(flag ? Integer.MAX_VALUE : 0);
    }

    @Override
    public Blaze getBukkitEntity() {
        return (Blaze) super.getBukkitEntity();
    }

}
