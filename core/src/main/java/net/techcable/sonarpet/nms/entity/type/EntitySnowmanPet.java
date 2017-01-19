package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySnowmanPet;

import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

@EntityPetType(petType = PetType.SNOWMAN)
public class EntitySnowmanPet extends EntityInsentientPet implements IEntitySnowmanPet {
    protected EntitySnowmanPet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }
}
