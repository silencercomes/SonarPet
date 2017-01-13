package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityVillagerPet;

import net.techcable.sonarpet.nms.entity.EntityAgeablePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.entity.Villager;

@EntityPetType(petType = PetType.VILLAGER)
public class EntityVillagerPet extends EntityAgeablePet implements IEntityVillagerPet {
    private final NMSInsentientEntity entity;

    protected EntityVillagerPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }

    @Override
    public void setProfession(Villager.Profession profession) {
        ((Villager) getBukkitEntity()).setProfession(profession);
    }

    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }
}
