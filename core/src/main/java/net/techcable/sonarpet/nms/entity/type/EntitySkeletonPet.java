package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.SkeletonType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySkeletonPet;
import com.dsh105.echopet.compat.api.plugin.EchoPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.entity.generators.EntityUndeadPetGenerator;
import net.techcable.sonarpet.nms.entity.generators.GeneratorClass;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.Material;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Stray;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@EntityHook(EntityHookType.SKELETON)
@GeneratorClass(EntityUndeadPetGenerator.class)
public class EntitySkeletonPet extends EntityInsentientPet implements IEntitySkeletonPet {
    protected EntitySkeletonPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    @Override
    public void initiateEntityPet() {
        super.initiateEntityPet();
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (getSkeletonType()) {
                    case STRAY:
                    case NORMAL:
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

    @Override
    @SuppressWarnings("deprecation") // We do a version check ;)
    public SkeletonType getSkeletonType() {
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            if (getBukkitEntity() instanceof WitherSkeleton) {
                return SkeletonType.WITHER;
            } else if (getBukkitEntity() instanceof Stray) {
                return SkeletonType.STRAY;
            } else {
                return SkeletonType.NORMAL;
            }
        } else {
            switch (getBukkitEntity().getSkeletonType()) {
                case WITHER:
                    return SkeletonType.WITHER;
                case STRAY:
                    return SkeletonType.STRAY;
                case NORMAL:
                    return SkeletonType.NORMAL;
                default:
                    throw new AssertionError("Unknown skeleton type!");
            }
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
            case NORMAL:
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
