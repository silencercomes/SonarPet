package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityGhastPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

@EntityHook(EntityHookType.GHAST)
public class EntityGhastPet extends EntityInsentientPet implements IEntityGhastPet {
    protected EntityGhastPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.OVERSIZE;
    }
}
