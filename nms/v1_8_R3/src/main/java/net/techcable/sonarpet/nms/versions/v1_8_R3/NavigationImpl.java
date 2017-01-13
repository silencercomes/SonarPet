package net.techcable.sonarpet.nms.versions.v1_8_R3;

import lombok.*;

import net.minecraft.server.v1_8_R3.Navigation;
import net.techcable.sonarpet.nms.PathEntity;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

@RequiredArgsConstructor
public class NavigationImpl implements net.techcable.sonarpet.nms.Navigation {
    private final Navigation handle;

    //
    // Breakage likely, check for bugs here
    //

    @Override
    public boolean canEnterDoors() {
        return handle.g();
    }

    @Override
    public void finish() {
        handle.n();
    }

    //
    // Unlikely to break, even across major versions
    // IE: never broken yet ^_^
    //

    @Override
    public PathEntity getPathToLocation(int blockX, int blockY, int blockZ) {
        return new PathEntityImpl(handle.a(blockX, blockY, blockZ));
    }

    @Override
    public PathEntity getPathTo(Entity other) {
        return new PathEntityImpl(handle.a(((CraftEntity) other).getHandle()));
    }

    @Override
    public void navigateTo(PathEntity path, double speed) {
        handle.a(((PathEntityImpl) path).handle, speed);
    }

    @RequiredArgsConstructor
    private static class PathEntityImpl implements PathEntity {
        /* package */ final net.minecraft.server.v1_8_R3.PathEntity handle;
    }
}
