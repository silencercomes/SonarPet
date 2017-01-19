package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityPigZombiePet;

import net.techcable.sonarpet.nms.entity.AbstractEntityZombiePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.Material;

@EntityPetType(petType = PetType.PIGZOMBIE)
public class EntityPigZombiePet extends AbstractEntityZombiePet implements IEntityPigZombiePet {
    public EntityPigZombiePet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }

    @SuppressWarnings("deprecation") // We only do it when it's safe
    public void setVillager(boolean flag) {
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            throw new UnsupportedOperationException("TODO");
        } else {
            getBukkitEntity().setVillager(flag);
        }
    }

    @Override
    protected Material getInitialItemInHand() {
        return Material.GOLD_SWORD;
    }
}
