package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityGuardianPet;
import net.techcable.sonarpet.nms.INMS;

import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.switching.EntitySwitchReason;
import net.techcable.sonarpet.nms.switching.GuardianType;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.Guardian;

@EntityPetType(petType = PetType.GUARDIAN)
public class EntityGuardianPet extends EntityInsentientPet implements IEntityGuardianPet {
    private NMSInsentientEntity entity;
    protected EntityGuardianPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }

    @Override
    public SizeCategory getSizeCategory() {
        return isElder() ? SizeCategory.GIANT : SizeCategory.LARGE;

    }

    @Override
    public NMSInsentientEntity getEntity() {
        return entity;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isElder() {
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            return getBukkitEntity() instanceof ElderGuardian;
        } else {
            return getBukkitEntity().isElder();
        }
    }

    @Override
    @SuppressWarnings("deprecation") // Only done on versions it's safe for
    public void setElder(boolean flag) {
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            entity = INMS.getInstance().switchType(
                    entity,
                    EntitySwitchReason.GUARDIAN_SWITCH,
                    flag ? GuardianType.ELDER : GuardianType.REGULAR
            );
        } else {
            getBukkitEntity().setElder(flag);
        }
    }

    @Override
    public Guardian getBukkitEntity() {
        return (Guardian) super.getBukkitEntity();
    }
}
