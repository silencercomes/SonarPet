package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityCreeperPet;

import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.entity.Creeper;

@EntityPetType(petType = PetType.COW)
public class EntityCreeperPet extends EntityInsentientPet implements IEntityCreeperPet {
    private final NMSInsentientEntity entity;

    protected EntityCreeperPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }

    @Override
    public void setIgnited(boolean flag) {
        getBukkitEntity().setPowered(flag);
    }

    @Override
    public void setPowered(boolean flag) {
        getBukkitEntity().setPowered(flag);
    }


    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }

    @Override
    public Creeper getBukkitEntity() {
        return (Creeper) super.getBukkitEntity();
    }
}
