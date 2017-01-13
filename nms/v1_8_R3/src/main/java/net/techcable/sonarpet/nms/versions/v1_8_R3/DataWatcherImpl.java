package net.techcable.sonarpet.nms.versions.v1_8_R3;

import lombok.*;

import net.minecraft.server.v1_8_R3.DataWatcher;

@RequiredArgsConstructor
public class DataWatcherImpl implements net.techcable.sonarpet.nms.DataWatcher {
    private final DataWatcher dataWatcher;

    //
    // Deobfuscated methods
    //

    @Override
    public void setBoolean(int id, boolean value) {
        dataWatcher.watch(id, value);
    }

    @Override
    public void setInteger(int id, int value) {
        dataWatcher.watch(id, value);
    }
}
