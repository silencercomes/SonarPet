package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySnowmanPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;

@EntityHook(EntityHookType.SNOWMAN)
public class EntitySnowmanPet extends EntityInsentientPet implements IEntitySnowmanPet {
    protected EntitySnowmanPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }
}
