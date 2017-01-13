package net.techcable.sonarpet.nms.versions.v1_8_R3;

import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.techcable.sonarpet.nms.INMS;
import com.google.common.collect.ImmutableMap;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.techcable.sonarpet.nms.BlockSoundData;
import net.techcable.sonarpet.nms.NMSEntity;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftSound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class NMSImpl implements INMS {
    @Override
    public boolean spawnEntity(NMSInsentientEntity wrapper, Location l) {
        EntityLiving entity = ((NMSEntityInsentientImpl) wrapper).getHandle();
        entity.spawnIn(((CraftWorld) l.getWorld()).getHandle());
        entity.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
        if (!l.getChunk().isLoaded()) {
            l.getChunk().load();
        }
        return entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public net.techcable.sonarpet.nms.DamageSource mobAttackDamageSource(LivingEntity entity) {
        return new DamageSourceImpl(DamageSource.mobAttack(((CraftLivingEntity) entity).getHandle()));
    }

    @Override
    public NMSEntity wrapEntity(Entity entity) {
        net.minecraft.server.v1_8_R3.Entity handle = ((CraftEntity) entity).getHandle();
        if (handle instanceof EntityPlayer) {
            return new NMSPlayerImpl((EntityPlayer) handle);
        } else if (handle instanceof EntityHorse) {
            return new NMSEntityHorseImpl((EntityHorse) handle);
        } else if (handle instanceof EntityInsentient) {
            return new NMSEntityInsentientImpl((EntityInsentient) handle);
        } else if (handle instanceof EntityLiving) {
            return new NMSLivingEntityImpl((EntityLiving) handle);
        } else {
            return new NMSEntityImpl(handle);
        }
    }

    @Override
    @SuppressWarnings("deprecation") // I know about ur stupid magic value warning mom
    public BlockSoundData getBlockSoundData(Material material) {
        return new BlockSoundDataImpl(Block.getById(material.getId()).stepSound);
    }

    @Override
    @SuppressWarnings("deprecation") // I know about ur stupid magic value warning mom
    public boolean isLiquid(Material block) {
        return Block.getById(block.getId()).getMaterial().isLiquid();
    }

    //
    // Utility methods
    //
    private static final ImmutableMap<String, Sound> BUKKIT_SOUNDS;
    static {
        ImmutableMap.Builder<String, Sound> soundsBuilder = ImmutableMap.builder();
        for (Sound bukkitSound : Sound.values()) {
            soundsBuilder.put(CraftSound.getSound(bukkitSound), bukkitSound);
        }
        BUKKIT_SOUNDS = soundsBuilder.build();
    }
    public static Sound toBukkitSound(String mojangSoundName) {
        Sound bukkitEffect = BUKKIT_SOUNDS.get(mojangSoundName);
        if (bukkitEffect == null) throw new AssertionError("No bukkit sound effect for mojang sound: " + mojangSoundName);
        return bukkitEffect;
    }
}
