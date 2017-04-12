package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityCreeperPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;

import org.bukkit.entity.Creeper;

@EntityHook(EntityHookType.CREEPER)
public class EntityCreeperPet extends EntityInsentientPet implements IEntityCreeperPet {
    protected EntityCreeperPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public void setIgnited(boolean flag) {
        getBukkitEntity().setPowered(flag);
    }

    @Override
    public void setPowered(boolean flag) {
        getBukkitEntity().setPowered(flag);
    }

    @Override
    public Creeper getBukkitEntity() {
        return (Creeper) super.getBukkitEntity();
    }
}
