package net.techcable.sonarpet.utils;

import lombok.*;

import org.bukkit.Bukkit;

public class Versioning {
    public static final String NMS_VERSION_STRING = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    public static final NmsVersion NMS_VERSION = NmsVersion.getVersion(NMS_VERSION_STRING);
    public static final int MAJOR_VERSION = parseMajorVersion(NMS_VERSION_STRING);

    private static int parseMajorVersion(String s) {
        return Integer.parseInt(s.split("_")[1]);
    }

    public static boolean isSupported() {
        return NMS_VERSION != null;
    }

    @Getter
    public enum NmsVersion {
        v1_6_R3,
        v1_7_R1,
        v1_7_R2,
        v1_7_R3,
        v1_7_R4,
        v1_8_R1,
        v1_8_R2,
        v1_8_R3,
        v1_9_R1;

        public static NmsVersion getVersion(String s) {
            try {
                return valueOf(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private final int majorVersion = parseMajorVersion(name());
    }
}
