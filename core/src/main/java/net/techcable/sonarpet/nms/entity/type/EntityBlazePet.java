package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityBlazePet;

import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.entity.Blaze;

@EntityPetType(petType = PetType.BLAZE)
public class EntityBlazePet extends EntityInsentientPet implements IEntityBlazePet {
    private final NMSInsentientEntity entity;

    protected EntityBlazePet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }

    @Override
    public void setOnFire(boolean flag) {
        getBukkitEntity().setFireTicks(flag ? Integer.MAX_VALUE : 0);
    }

    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }

    @Override
    public Blaze getBukkitEntity() {
        return (Blaze) super.getBukkitEntity();
    }

}
