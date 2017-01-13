package net.techcable.sonarpet.nms.versions.v1_10_R1;

import lombok.*;

import net.minecraft.server.v1_10_R1.SoundEffectType;
import net.techcable.sonarpet.nms.BlockSoundData;

@RequiredArgsConstructor
public class BlockSoundDataImpl implements BlockSoundData {
    private final SoundEffectType handle;

    @Override
    public float getVolume() {
        return handle.a();
    }

    @Override
    public float getPitch() {
        return handle.b();
    }

    @Override
    public boolean equals(Object o) { // We need this!!
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockSoundDataImpl that = (BlockSoundDataImpl) o;

        return handle.equals(that.handle);
    }

    @Override
    public int hashCode() {
        return handle.hashCode();
    }
}

