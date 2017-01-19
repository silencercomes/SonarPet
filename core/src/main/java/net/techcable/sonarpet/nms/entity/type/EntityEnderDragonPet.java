package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityEnderDragonPet;

import net.techcable.sonarpet.nms.entity.EntityNoClipPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.generators.EntityEnderdragonPetGenerator;
import net.techcable.sonarpet.nms.entity.generators.GeneratorClass;

@EntityPetType(petType = PetType.ENDERDRAGON)
@GeneratorClass(EntityEnderdragonPetGenerator.class)
public class EntityEnderDragonPet extends EntityNoClipPet implements IEntityEnderDragonPet {
    protected EntityEnderDragonPet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.GIANT;
    }
}
