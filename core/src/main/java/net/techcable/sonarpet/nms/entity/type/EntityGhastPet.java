package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityGhastPet;

import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

@EntityPetType(petType = PetType.GHAST)
public class EntityGhastPet extends EntityInsentientPet implements IEntityGhastPet {
    private final NMSInsentientEntity entity;
    protected EntityGhastPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.OVERSIZE;
    }

    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }
}
