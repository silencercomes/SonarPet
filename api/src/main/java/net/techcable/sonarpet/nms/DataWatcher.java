package net.techcable.sonarpet.nms;

public interface DataWatcher {
        void setBoolean(int id, boolean value);

        void setInteger(int id, int value);

        void setByte(int id, byte value);

        void setByte(int id, int bit, boolean flag);

}
