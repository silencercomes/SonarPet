package net.techcable.sonarpet.nms.entity.type;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetData;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityWolfPet;

import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityAgeablePet;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Wolf;

@EntityHook(EntityHookType.WOLF)
public class EntityWolfPet extends EntityAgeablePet implements IEntityWolfPet {
        protected EntityWolfPet(IPet pet, NMSInsentientEntity entity, EntityHookType hookType) {
                super(pet, entity, hookType);
        }

        public static final int ANGRY_METADATA_ID = Versioning.NMS_VERSION.getMetadataId("ANGRY_METADATA_ID");
        public static final int ANGRY_METADATA_ID_BIT = Versioning.NMS_VERSION.getMetadataId("ANGRY_METADATA_ID_BIT");

        @Override
        public void initiateEntityPet() {
                super.initiateEntityPet();
                setTamed(true); // Tame
        }

        @Override
        public void setTamed(boolean flag) {
                if (isAngry() && flag) {
                        setAngry(false);
                }

                getBukkitEntity().setTamed(flag);

                if (!flag) {
                        getPet().getPetData().remove(PetData.TAMED);
                } else if (!getPet().getPetData().contains(PetData.TAMED)) {
                        this.getPet().getPetData().add(PetData.TAMED);
                }
        }

        @Override
        public void setAngry(boolean flag) {
                if (flag) {
                        if (getBukkitEntity().isTamed()) {
                                setTamed(false);
                        }
                        if (!getPet().getPetData().contains(PetData.ANGRY)) {
                                this.getPet().getPetData().add(PetData.ANGRY);
                        }
                } else {
                        getPet().getPetData().remove(PetData.ANGRY);
                }

                getEntity().getDataWatcher().setByte(ANGRY_METADATA_ID, ANGRY_METADATA_ID_BIT, flag);
                //getBukkitEntity().setAngry(flag);
        }

        public boolean isAngry() {
                return getBukkitEntity().isAngry();
        }

        @Override
        public void setCollarColor(DyeColor dc) {
                getBukkitEntity().setCollarColor(dc);
        }

        @Override
        public Wolf getBukkitEntity() {
                return (Wolf) super.getBukkitEntity();
        }
}
