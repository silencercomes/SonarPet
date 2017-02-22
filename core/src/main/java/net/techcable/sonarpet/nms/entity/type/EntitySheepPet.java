package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySheepPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityAgeablePet;

import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;

@EntityHook(EntityHookType.SHEEP)
public class EntitySheepPet extends EntityAgeablePet implements IEntitySheepPet {
    protected EntitySheepPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public void setColor(int i) {
        DyeColor color = DyeColor.getByWoolData((byte) i);
        if ((byte) i != i || color == null) {
            throw new IllegalArgumentException("Invalid wool color id: " + i);
        }
        getBukkitEntity().setColor(color);
    }

    @Override
    public void setSheared(boolean flag) {
        getBukkitEntity().setSheared(flag);
    }

    @Override
    public void makeStepSound() {
        getEntity().playSound(SafeSound.SHEEP_STEP, 0.15F, 1.0F);
    }

    @Override
    public Sheep getBukkitEntity() {
        return (Sheep) super.getBukkitEntity();
    }
}
