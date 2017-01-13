package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityBatPet;

import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.entity.Bat;

@EntityPetType(petType = PetType.BAT)
public class EntityBatPet extends EntityInsentientPet implements IEntityBatPet {
    private final NMSInsentientEntity entity;

    protected EntityBatPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }


    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.TINY;
    }

    @Override
    public void setHanging(boolean flag) {
        getBukkitEntity().setAwake(!flag);
    }

    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }

    @Override
    public Bat getBukkitEntity() {
        return (Bat) super.getBukkitEntity();
    }

}
