package net.techcable.sonarpet;

import java.util.Objects;

import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.plugin.EchoPet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Indicates that a pet's spawning has been cancelled via {@link com.dsh105.echopet.compat.api.event.PetPreSpawnEvent}.
 */
public class CancelledSpawnException extends Exception {
    private final Player owner;
    private final PetType petType;
    public CancelledSpawnException(Player owner, PetType petType) {
        this.owner = Objects.requireNonNull(owner);
        this.petType = Objects.requireNonNull(petType);
    }
    public void sendMessage() {
        owner.sendMessage(EchoPet.getPrefix() + ChatColor.YELLOW + "Pet spawn was cancelled externally.");
    }
}
