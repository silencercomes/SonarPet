package net.techcable.sonarpet.particles;

import lombok.*;

import com.dsh105.commodus.reflection.Reflection;

import org.apache.commons.lang.ArrayUtils;

@Getter
public class v18ParticleBuilder extends PacketParticleBuilder {
    private boolean forced = true;

    public v18ParticleBuilder(Particle type, float speed, int amount) {
        super(type, speed, amount);
    }

    @Override
    protected Object getNMSParticleType() {
        Class var6 = Reflection.getNMSClass("EnumParticle");
        return var6.getEnumConstants()[this.getType().getId()];
    }

    @Override
    protected void setupPacket(Object packet) {
        super.setupPacket(packet);
        PARTICLE_PACKET_TYPE.putObject(packet, 1, createData());
        PARTICLE_PACKET_TYPE.putBoolean(packet, 0, isForced());
    }

    private int[] createData() {
        return hasBlockData() ? new int[] {getBlockType().getId(), getBlockMeta()} : ArrayUtils.EMPTY_INT_ARRAY;
    }
}
