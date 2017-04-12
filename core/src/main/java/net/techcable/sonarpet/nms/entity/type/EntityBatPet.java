package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityBatPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;

import org.bukkit.entity.Bat;

@EntityHook(EntityHookType.BAT)
public class EntityBatPet extends EntityInsentientPet implements IEntityBatPet {
    protected EntityBatPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public void initiateEntityPet() {
        super.initiateEntityPet();
        this.getBukkitEntity().setAwake(true);
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
    public Bat getBukkitEntity() {
        return (Bat) super.getBukkitEntity();
    }

}
