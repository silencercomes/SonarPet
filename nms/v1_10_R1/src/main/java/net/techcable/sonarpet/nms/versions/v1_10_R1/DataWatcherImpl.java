package net.techcable.sonarpet.nms.versions.v1_10_R1;

import lombok.*;

import java.lang.Integer;

import net.minecraft.server.v1_10_R1.DataWatcher;
import net.minecraft.server.v1_10_R1.DataWatcherObject;
import net.minecraft.server.v1_10_R1.DataWatcherRegistry;
import net.minecraft.server.v1_10_R1.DataWatcherSerializer;

@RequiredArgsConstructor
public class DataWatcherImpl implements net.techcable.sonarpet.nms.DataWatcher {
    private final DataWatcher dataWatcher;

    //
    // Unlikely to break, even across major versions
    // IE: never broken yet ^_^
    //

    private static final DataWatcherSerializer<Boolean> BOOLEAN_SERIALIZER = DataWatcherRegistry.h;
    private static final DataWatcherSerializer<Integer> INTEGER_SERIALIZER = DataWatcherRegistry.b;
    private static final DataWatcherSerializer<Byte> BYTE_SERIALIZER = DataWatcherRegistry.a;

    //
    // Deobfuscated methods
    //

    @Override
    public void setBoolean(int id, boolean value) {
        dataWatcher.set(new DataWatcherObject<>(id, BOOLEAN_SERIALIZER), value);
    }

    @Override
    public void setInteger(int id, int value) {
        dataWatcher.set(new DataWatcherObject<>(id, INTEGER_SERIALIZER), value);
    }

    @Override
    public void setByte(int id, byte value) {
	//byte b = (new Integer(value)).byteValue();
        dataWatcher.set(new DataWatcherObject<>(id, BYTE_SERIALIZER), value);
    }

    @Override
    public void setByte(int id, int bit, boolean flag) {
            DataWatcherObject<Byte> dw = new DataWatcherObject<>(id, BYTE_SERIALIZER);
            byte b = dataWatcher.get( dw);
            if (flag == true) {
                    b |= (1 << bit);
            } else {
                    b &= ~(1 << bit);
            }
            dataWatcher.set(new DataWatcherObject<>(id, BYTE_SERIALIZER), b);
    }

}
