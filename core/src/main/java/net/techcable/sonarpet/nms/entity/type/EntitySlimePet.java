package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySlimePet;

import net.techcable.sonarpet.nms.entity.AbstractEntitySlimePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

@EntityPetType(petType = PetType.SLIME)
public class EntitySlimePet extends AbstractEntitySlimePet implements IEntitySlimePet {
    protected EntitySlimePet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }
}
