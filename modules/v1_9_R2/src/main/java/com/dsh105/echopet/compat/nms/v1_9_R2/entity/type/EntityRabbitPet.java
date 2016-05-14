package com.dsh105.echopet.compat.nms.v1_9_R2.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.EntitySize;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityRabbitPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R2.entity.EntityAgeablePet;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataType;

import net.minecraft.server.v1_9_R2.World;

import org.bukkit.Sound;
import org.bukkit.entity.Rabbit;

@EntitySize(width = 0.6F, height = 0.7F)
@EntityPetType(petType = PetType.RABBIT)
public class EntityRabbitPet extends EntityAgeablePet implements IEntityRabbitPet {

    public static final MetadataKey<Integer> RABBIT_TYPE_METADATA = new MetadataKey<>(12, MetadataType.VAR_INT);

    private int jumpDelay;

    public EntityRabbitPet(World world) {
        super(world);
    }

    public EntityRabbitPet(World world, IPet pet) {
        super(world, pet);
        this.jumpDelay = this.random.nextInt(15) + 10;
    }

    @Override
    protected Sound getIdleSound() {
        return Sound.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_RABBIT_HURT;
    }

    @Override
    public Rabbit.Type getRabbitType() {
        return CraftMagicMapping.fromMagic(getDatawatcher().get(RABBIT_TYPE_METADATA));
    }
    
    @Override
    public void setRabbitType(Rabbit.Type type) {
        getDatawatcher().set(RABBIT_TYPE_METADATA, CraftMagicMapping.toMagic(type));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(RABBIT_TYPE_METADATA, CraftMagicMapping.toMagic(Rabbit.Type.WHITE));
    }
    
    @Override
    public void onLive() {
        super.onLive();
        // same as the slime
        if (this.onGround && this.jumpDelay-- <= 0) {
            NMS.jump(this);
            this.jumpDelay = this.random.nextInt(15) + 10;
            this.world.broadcastEntityEffect(this, (byte) 1);
        }
    }

    // TODO: Replace this nonesne with something better

    /**
     * Magic bunny mappings from CraftRabbit
     */
    private static class CraftMagicMapping {
        private static final int[] types = new int[Rabbit.Type.values().length];
        private static final Rabbit.Type[] reverse = new Rabbit.Type[Rabbit.Type.values().length];

        static {
            set(Rabbit.Type.BROWN, 0);
            set(Rabbit.Type.WHITE, 1);
            set(Rabbit.Type.BLACK, 2);
            set(Rabbit.Type.BLACK_AND_WHITE, 3);
            set(Rabbit.Type.GOLD, 4);
            set(Rabbit.Type.SALT_AND_PEPPER, 5);
            set(Rabbit.Type.THE_KILLER_BUNNY, 99);
        }

        private CraftMagicMapping() {
        }

        private static void set(Rabbit.Type type, int value) {
            types[type.ordinal()] = value;
            if(value < reverse.length) {
                reverse[value] = type;
            }

        }

        public static Rabbit.Type fromMagic(int magic) {
            return magic >= 0 && magic < reverse.length?reverse[magic]:(magic == 99? Rabbit.Type.THE_KILLER_BUNNY:null);
        }

        public static int toMagic(Rabbit.Type type) {
            return types[type.ordinal()];
        }
    }
}
