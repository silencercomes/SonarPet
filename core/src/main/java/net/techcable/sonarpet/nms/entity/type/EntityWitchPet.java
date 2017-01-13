package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityWitchPet;

import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

@EntityPetType(petType = PetType.WITCH)
public class EntityWitchPet extends EntityInsentientPet implements IEntityWitchPet {
    private final NMSInsentientEntity entity;

    protected EntityWitchPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }


    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }
}
