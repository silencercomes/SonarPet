package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityZombiePet;

import net.techcable.sonarpet.nms.entity.AbstractEntityZombiePet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.generators.EntityUndeadPetGenerator;
import net.techcable.sonarpet.nms.entity.generators.GeneratorClass;
import com.dsh105.echopet.compat.api.entity.ZombieType;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.Material;

@EntityPetType(petType = PetType.PIGZOMBIE)
@GeneratorClass(EntityUndeadPetGenerator.class)
public class EntityZombiePet extends AbstractEntityZombiePet implements IEntityZombiePet {
    public EntityZombiePet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setZombieType(ZombieType type) {
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            throw new UnsupportedOperationException("TODO");
        } else {
            switch (type) {
                case REGULAR:
                    getBukkitEntity().setVillager(false);
                case VILLAGER:
                    getBukkitEntity().setVillager(true);
                default:
                    throw new IllegalArgumentException(type + " isn't supported on " + Versioning.NMS_VERSION);
            }
        }
    }

    @Override
    protected Material getInitialItemInHand() {
        return Material.IRON_SPADE;
    }
}
