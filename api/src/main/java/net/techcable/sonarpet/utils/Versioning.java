package net.techcable.sonarpet.utils;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;

public class Versioning {
    public static final String NMS_VERSION_STRING;
    @Nonnull
    public static final NmsVersion NMS_VERSION;
    public static final int MAJOR_VERSION;
    public static final String NMS_PACKAGE;
    public static final String OBC_PACKAGE;

    static {
        String[] parts = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        if (parts.length > 3) {
            NMS_VERSION_STRING = parts[3]; // We can determine NMS version from the package name
            NMS_PACKAGE = "net.minecraft.server." + NMS_VERSION_STRING;
            OBC_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION_STRING;
        } else {
            // Oh boy, the package doesn't have a version string
            NMS_PACKAGE = "net.minecraft.server";
            OBC_PACKAGE = "org.bukkit.craftbukkit";
            NMS_VERSION_STRING = NmsVersion.LATEST.toString();
        }
        try {
            NMS_VERSION = NmsVersion.valueOf(NMS_VERSION_STRING);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unknown version: " + NMS_VERSION_STRING);
        }
        MAJOR_VERSION = NMS_VERSION.getMajorVersion();
    }

}
