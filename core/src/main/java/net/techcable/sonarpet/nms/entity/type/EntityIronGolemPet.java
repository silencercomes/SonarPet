package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityIronGolemPet;

import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.entity.Entity;

@EntityPetType(petType = PetType.IRONGOLEM)
public class EntityIronGolemPet extends EntityInsentientPet implements IEntityIronGolemPet {
    private final NMSInsentientEntity entity;
    protected EntityIronGolemPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }

    @Override
    public boolean attack(Entity entity) {
        boolean flag = super.attack(entity);
        if (flag) {
            // TODO: fix
            //this.world.broadcastEntityEffect(this, (byte) 4);
            getEntity().setUpwardsMotion(0.4000000059604645D);
            getEntity().playSound(SafeSound.IRONGOLEM_ATTACK, 1.0F, 1.0F);
        }
        return flag;
    }

    @Override
    public void makeStepSound() {
        getEntity().playSound(SafeSound.IRONGOLEM_STEP, 1.0F, 1.0F);
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.LARGE;
    }

    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }
}
