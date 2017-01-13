package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityMagmaCubePet;

import net.techcable.sonarpet.nms.entity.AbstractEntitySlimePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

@EntityPetType(petType = PetType.MAGMACUBE)
public class EntityMagmaCubePet extends AbstractEntitySlimePet implements IEntityMagmaCubePet {
    private final NMSInsentientEntity entity;

    protected EntityMagmaCubePet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }

    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }
}
