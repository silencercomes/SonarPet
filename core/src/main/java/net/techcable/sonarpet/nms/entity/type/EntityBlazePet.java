package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityBlazePet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.utils.Versioning;

//import org.bukkit.entity.Blaze;
//import org.bukkit.Bukkit;

@EntityHook(EntityHookType.BLAZE)
public class EntityBlazePet extends EntityInsentientPet implements IEntityBlazePet {
    protected EntityBlazePet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
        super(pet, entity, hookType);
    }

    public static final int ONFIRE_METADATA_ID = Versioning.NMS_VERSION.getMetadataId("BLAZE_ONFIRE_METADATA_ID");

    @Override
    public void setOnFire(boolean flag) {
            byte b1 = 1;
            byte b0 = 0;
        getEntity().getDataWatcher().setByte(ONFIRE_METADATA_ID, (flag == true)?b1:b0);
//	Blaze b = (Blaze) getBukkitEntity();
//      b.setFireTicks(Integer.MAX_VALUE);
    }

/*    @Override
    public Blaze getBukkitEntity() {
        return (Blaze) super.getBukkitEntity();
    }
//*/
}
