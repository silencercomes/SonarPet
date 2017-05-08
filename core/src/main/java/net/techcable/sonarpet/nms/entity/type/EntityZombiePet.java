package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityZombiePet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.AbstractEntityZombiePet;
import net.techcable.sonarpet.nms.entity.generators.EntityUndeadPetGenerator;
import net.techcable.sonarpet.nms.entity.generators.GeneratorClass;

import org.bukkit.Material;

@EntityHook({
        EntityHookType.ZOMBIE,
        EntityHookType.VILLAGER_ZOMBIE,
        EntityHookType.HUSK_ZOMBIE
})
@GeneratorClass(EntityUndeadPetGenerator.class)
public class EntityZombiePet extends AbstractEntityZombiePet {
    public EntityZombiePet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    protected Material getInitialItemInHand() {
        return Material.IRON_SPADE;
    }
}
