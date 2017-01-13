package net.techcable.sonarpet.nms.versions.v1_8_R3;

import lombok.*;

import net.minecraft.server.v1_8_R3.Block;
import net.techcable.sonarpet.nms.BlockSoundData;

@RequiredArgsConstructor
public class BlockSoundDataImpl implements BlockSoundData {
    private final Block.StepSound handle;

    @Override
    public float getVolume() {
        return handle.getVolume1();
    }

    @Override
    public float getPitch() {
        return handle.getVolume2();
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

