package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityOcelotPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityAgeablePet;

import org.bukkit.entity.Ocelot;

@EntityHook(EntityHookType.OCELOT)
public class EntityOcelotPet extends EntityAgeablePet implements IEntityOcelotPet {
    protected EntityOcelotPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }


    @Override
    public void initiateEntityPet() {
        super.initiateEntityPet();
        getBukkitEntity().setTamed(true);
        getBukkitEntity().setCatType(Ocelot.Type.BLACK_CAT);
        getBukkitEntity().setOwner(getPlayerOwner());
    }

    @Override
    public void setCatType(int type) {
        getBukkitEntity().setCatType(Ocelot.Type.getType(type));
    }

    @Override
    public Ocelot getBukkitEntity() {
        return (Ocelot) super.getBukkitEntity();
    }
}
