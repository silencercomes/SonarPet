package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityEnderDragonPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.entity.EntityNoClipPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.generators.CustomMotionPetGenerator;
import net.techcable.sonarpet.nms.entity.generators.GeneratorClass;

@EntityHook(EntityHookType.ENDER_DRAGON)
@GeneratorClass(CustomMotionPetGenerator.class)
public class EntityEnderDragonPet extends EntityNoClipPet implements IEntityEnderDragonPet {
    protected EntityEnderDragonPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.GIANT;
    }
}
