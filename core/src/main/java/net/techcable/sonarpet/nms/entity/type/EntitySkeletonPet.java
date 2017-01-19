package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySkeletonPet;
import com.dsh105.echopet.compat.api.plugin.EchoPet;

import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.entity.generators.EntityUndeadPetGenerator;
import net.techcable.sonarpet.nms.entity.generators.GeneratorClass;
import com.dsh105.echopet.compat.api.entity.SkeletonType;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.Material;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Stray;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@EntityPetType(petType = PetType.SKELETON)
@GeneratorClass(EntityUndeadPetGenerator.class)
public class EntitySkeletonPet extends EntityInsentientPet implements IEntitySkeletonPet {
    protected EntitySkeletonPet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }

    @Override
    public void initiateEntityPet() {
        super.initiateEntityPet();
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (getSkeletonType()) {
                    case STRAY:
                    case REGULAR:
                        getBukkitEntity().getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
                        break;
                    case WITHER:
                        getBukkitEntity().getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
                        break;
                    default:
                        throw new AssertionError("Unknown skeleton type: " + getSkeletonType());
                }
            }
        }.runTaskLater(EchoPet.getPlugin(), 5L);
    }

    @SuppressWarnings("deprecation")
    public SkeletonType getSkeletonType() {
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            if (getBukkitEntity() instanceof WitherSkeleton) {
                return SkeletonType.WITHER;
            } else if (getBukkitEntity() instanceof Stray) {
                return SkeletonType.STRAY;
            } else {
                return SkeletonType.REGULAR;
            }
        } else {
            switch (getBukkitEntity().getSkeletonType()) {
                case WITHER:
                    return SkeletonType.WITHER;
                case STRAY:
                    return SkeletonType.STRAY;
                case NORMAL:
                    return SkeletonType.REGULAR;
                default:
                    throw new AssertionError("Unknown skeleton type!");
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setWither(boolean flag) {
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            throw new UnsupportedOperationException("TODO");
        } else {
            getBukkitEntity().setSkeletonType(flag ? Skeleton.SkeletonType.WITHER : Skeleton.SkeletonType.NORMAL);
        }
    }

    @Override
    public void makeStepSound() {
        getEntity().playSound(SafeSound.SKELETON_STEP, 0.15F, 1.0F);
    }

    @Override
    public SizeCategory getSizeCategory() {
        switch (getSkeletonType()) {
            case STRAY:
            case REGULAR:
                return SizeCategory.REGULAR;
            case WITHER:
                return SizeCategory.LARGE;
            default:
                throw new AssertionError("Unknown skeleton type: " + getSkeletonType());
        }
    }

    @Override
    public Skeleton getBukkitEntity() {
        return (Skeleton) super.getBukkitEntity();
    }
}
