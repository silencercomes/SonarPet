package net.techcable.sonarpet.nms;

import org.bukkit.entity.Entity;

public interface Navigation {
    boolean canEnterDoors();

    void finish();

    PathEntity getPathToLocation(int blockX, int blockY, int blockZ);

    PathEntity getPathTo(Entity other);

    void navigateTo(PathEntity path, double speed);
}
