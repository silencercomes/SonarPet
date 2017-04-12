package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityWitchPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;

@EntityHook(EntityHookType.WITCH)
public class EntityWitchPet extends EntityInsentientPet implements IEntityWitchPet {
    protected EntityWitchPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }
}
