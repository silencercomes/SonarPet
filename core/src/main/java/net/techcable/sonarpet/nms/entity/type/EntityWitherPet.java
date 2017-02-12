package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityWitherPet;

import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.utils.Versioning;

@EntityPetType(petType = PetType.WITHER)
public class EntityWitherPet extends EntityInsentientPet implements IEntityWitherPet {
    private final NMSInsentientEntity entity;

    protected EntityWitherPet(IPet pet, NMSInsentientEntity entity) {
        super(pet);
        this.entity = entity;
    }


    public static final int SHIELDED_METADATA_ID = Versioning.NMS_VERSION.getMetadataId("WITHER_SHIELDED_METADATA_ID");

    public void setShielded(boolean flag) {
        getEntity().getDataWatcher().setInteger(SHIELDED_METADATA_ID, Integer.MAX_VALUE);
        getBukkitEntity().setHealth((float) (flag ? 150 : 300));
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
