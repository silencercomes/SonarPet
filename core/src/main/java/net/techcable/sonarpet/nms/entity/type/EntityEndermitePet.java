package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityEndermitePet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityNoClipPet;
import net.techcable.sonarpet.particles.Particle;

import org.bukkit.Location;

@EntityHook(EntityHookType.ENDERMITE)
public class EntityEndermitePet extends EntityNoClipPet implements IEntityEndermitePet {
    protected EntityEndermitePet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.TINY;
    }

    @Override
    public void makeStepSound() {
        getEntity().playSound(SafeSound.ENDERMITE_STEP, 0.15F, 1.0F);
    }

    @Override
    public void onLive() {
        super.onLive();
        for (int i = 0; i < 2; i++) {
            Particle.PORTAL.builder()
                    .at(new Location(
                            getBukkitEntity().getWorld(),
                            getBukkitEntity().getLocation().getX() + (this.random().nextDouble() - 0.5D) * getEntity().getWidth(),
                            getBukkitEntity().getLocation().getY() + (this.random().nextDouble()) * getEntity().getLength(),
                            getBukkitEntity().getLocation().getZ() + (this.random().nextDouble() - 0.5D) * getEntity().getWidth()
                    ))
                    .offset(
                            (this.random().nextDouble() - 0.5D) * 2.0D,
                            -this.random().nextDouble(),
                            (this.random().nextDouble() - 0.5D) * 2.0D
                    )
                    .show();
        }
    }
}
