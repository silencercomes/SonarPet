package net.techcable.sonarpet.particles;

import com.google.common.base.Preconditions;

import net.techcable.pineapple.reflection.Reflection;
import net.techcable.sonarpet.utils.reflection.MinecraftReflection;
import net.techcable.sonarpet.utils.reflection.PacketType;

import org.bukkit.entity.Player;

public abstract class PacketParticleBuilder extends ParticleBuilder {
    public PacketParticleBuilder(Particle type, float speed, int amount) {
        super(type, speed, amount);
    }
    public static final PacketType PARTICLE_PACKET_TYPE = PacketType.forName("PlayOutWorldParticles");
    public void show(Player player) {
        Object packet = PARTICLE_PACKET_TYPE.newPacket();
        setupPacket(packet);
        Preconditions.checkState(getPosition() != null, "Position not set");
        Preconditions.checkState(getPosition() != null, "Offset not set");
        MinecraftReflection.sendPacket(player, packet);
    }

    protected abstract Object getNMSParticleType();

    protected void setupPacket(Object packet) {
        PARTICLE_PACKET_TYPE.putObject(packet, 0, this.getNMSParticleType());
        PARTICLE_PACKET_TYPE.putFloat(packet, 0, (float) getPosition().getX());
        PARTICLE_PACKET_TYPE.putFloat(packet, 1, (float) getPosition().getY());
        PARTICLE_PACKET_TYPE.putFloat(packet, 2, (float) getPosition().getZ());
        PARTICLE_PACKET_TYPE.putFloat(packet, 3, (float) getOffset().getX());
        PARTICLE_PACKET_TYPE.putFloat(packet, 4, (float) getOffset().getY());
        PARTICLE_PACKET_TYPE.putFloat(packet, 5, (float) getOffset().getZ());
        PARTICLE_PACKET_TYPE.putFloat(packet, 6, getSpeed());
        PARTICLE_PACKET_TYPE.putInt(packet, 0, getAmount());
    }
}
