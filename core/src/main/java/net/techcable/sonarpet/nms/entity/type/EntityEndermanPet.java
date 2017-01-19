package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityEndermanPet;

import net.techcable.sonarpet.nms.entity.EntityNoClipPet;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.entity.Enderman;

@EntityPetType(petType = PetType.ENDERMAN)
public class EntityEndermanPet extends EntityNoClipPet implements IEntityEndermanPet {
    protected EntityEndermanPet(IPet pet, NMSInsentientEntity entity) {
        super(pet, entity);
    }
    private static final int SCREAMING_METADATA_ID = Versioning.NMS_VERSION.getMetadataId("ENDERMAN_SCREAMING_METADATA_ID");

    @Override
    public void setScreaming(boolean flag) {
        getEntity().getDataWatcher().setBoolean(SCREAMING_METADATA_ID, flag);
    }

    @Override
    public Enderman getBukkitEntity() {
        return (Enderman) super.getBukkitEntity();
    }
}
