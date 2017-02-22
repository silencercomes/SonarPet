package net.techcable.sonarpet.nms.entity.type;

import java.lang.invoke.MethodHandle;

import com.dsh105.echopet.compat.api.entity.HorseArmour;
import com.dsh105.echopet.compat.api.entity.HorseMarking;
import com.dsh105.echopet.compat.api.entity.HorseType;
import com.dsh105.echopet.compat.api.entity.HorseVariant;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityHorsePet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.nms.BlockSoundData;
import net.techcable.sonarpet.nms.INMS;
import net.techcable.sonarpet.nms.NMSEntityHorse;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityAgeablePet;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

@EntityHook(EntityHookType.HORSE)
public class EntityHorsePet extends EntityAgeablePet implements IEntityHorsePet {
    protected EntityHorsePet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }


    private int rearingCounter = 0;
    int stepSoundCount = 0;

    @Override
    public void initiateEntityPet() {
        super.initiateEntityPet();
        ((Tameable) getBukkitEntity()).setOwner(Bukkit.getOfflinePlayer(getPet().getOwnerUUID()));
    }

    @Override
    public void setSaddled(boolean flag) {
        ((HorseInventory) ((InventoryHolder) getBukkitEntity()).getInventory())
                .setSaddle(flag ? new ItemStack(Material.SADDLE, 1) : null);
    }

    public HorseType getHorseType() {
        return getEntity().getHorseType();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setHorseType(HorseType newType) {
        if (newType != HorseType.NORMAL) {
            this.setArmour(HorseArmour.NONE);
        }
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            throw new UnsupportedOperationException("TODO");
        } else {
            getEntity().setHorseType(newType);
        }
    }

    @Override
    public void setVariant(HorseVariant v, HorseMarking m) {
        getEntity().setColor(v.getBukkitColour());
        getEntity().setStyle(m.getBukkitStyle());
    }

    @Override
    public void setArmour(HorseArmour a) {
        ((HorseInventory) ((InventoryHolder) getBukkitEntity()).getInventory())
                .setArmor(a.getMaterial() == null ? null : new ItemStack(a.getMaterial(), 1));
    }

    @Override
    public void setChested(boolean flag) {
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            if (!(getBukkitEntity() instanceof ChestedHorse)) {
                /*
                 * To fail silently, or to throw an exception; that is the question.
                 * Whether is nobler to be confusing and get a bug report,
                 * or be annoying and get a bug report?
                 */
                return;
            }
        }
        getEntity().setCarryingChest(flag);
    }

    private void setRearing(boolean b) {
        getEntity().setRearing(b);
    }

    @Override
    public boolean attack(Entity entity) {
        boolean flag = super.attack(entity);
        if (flag) {
            this.setRearing(true);
            switch (getHorseType()) {
                case DONKEY:
                    getEntity().playSound(SafeSound.DONKEY_ANGRY, 1, 1);
                    break;
                default:
                    getEntity().playSound(SafeSound.HORSE_ANGRY, 1, 1);
                    break;
            }
        }
        return flag;
    }

    @Override
    public void makeStepSound(int i, int j, int k, Material block) {
        BlockSoundData soundData = INMS.getInstance().getBlockSoundData(block);

        if (getBukkitEntity().getWorld().getBlockAt(i, j + 1, k).getType() == Material.SNOW) {
            soundData = INMS.getInstance().getBlockSoundData(Material.SNOW);
        }

        if (!INMS.getInstance().isLiquid(block)) {
            if (!getEntity().getPassengers().isEmpty() && getHorseType() != HorseType.NORMAL && getHorseType() != HorseType.MULE) {
                ++this.stepSoundCount;
                if (this.stepSoundCount > 5 && this.stepSoundCount % 3 == 0) {
                    getEntity().playSound(SafeSound.HORSE_GALLOP, soundData.getVolume() * 0.15F, soundData.getPitch());
                    if (getHorseType() == HorseType.NORMAL && this.random() .nextInt(10) == 0) {
                        getEntity().playSound(SafeSound.HORSE_BREATHE, soundData.getVolume() * 0.6F, soundData.getPitch());
                    }
                } else if (this.stepSoundCount <= 5) {
                    getEntity().playSound(SafeSound.HORSE_STEP_WOOD, soundData.getVolume() * 0.15F, soundData.getPitch());
                }
            } else if (soundData.equals(BlockSoundData.WOOD)) {
                getEntity().playSound(SafeSound.HORSE_STEP_WOOD, soundData.getVolume() * 0.15F, soundData.getPitch());
            } else {
                getEntity().playSound(SafeSound.HORSE_STEP, soundData.getVolume() * 0.15F, soundData.getPitch());
            }
        }
    }

    @Override
    public void move(float sideMot, float forwMot, MethodHandle superMoveFunction) {
        super.move(sideMot, forwMot, superMoveFunction);
        if (forwMot <= 0.0F) {
            this.stepSoundCount = 0;
        }
    }

    @Override
    public SizeCategory getSizeCategory() {
        if (this.isBaby()) {
            return SizeCategory.TINY;
        } else {
            return SizeCategory.LARGE;
        }
    }

    @Override
    public void onLive() {
        super.onLive();
        if (rearingCounter > 0 && ++rearingCounter > 20) {
            setRearing(false);
        }
    }

    @Override
    public void doJumpAnimation() {
        getEntity().playSound(SafeSound.HORSE_JUMP, 0.4F, 1.0F);
        this.rearingCounter = 1;
        setRearing(true);
    }

    @Override
    public NMSEntityHorse getEntity() {
        return (NMSEntityHorse) super.getEntity();
    }
}
