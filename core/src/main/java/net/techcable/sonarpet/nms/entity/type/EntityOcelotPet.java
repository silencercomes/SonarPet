package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityOcelotPet;

import net.techcable.sonarpet.nms.entity.EntityAgeablePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.entity.Ocelot;

@EntityPetType(petType = PetType.OCELOT)
public class EntityOcelotPet extends EntityAgeablePet implements IEntityOcelotPet {
    private final NMSInsentientEntity entity;

    protected EntityOcelotPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }


    @Override
    public void initiateEntityPet() {
        super.initiateEntityPet();
        getBukkitEntity().setTamed(true);
        getBukkitEntity().setCatType(Ocelot.Type.BLACK_CAT);
        getBukkitEntity().setOwner(getPlayerOwner());
    }

    @Override
    public void setCatType(int type) {
        getBukkitEntity().setCatType(Ocelot.Type.getType(type));
    }
    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }

    @Override
    public Ocelot getBukkitEntity() {
        return (Ocelot) super.getBukkitEntity();
    }
}
