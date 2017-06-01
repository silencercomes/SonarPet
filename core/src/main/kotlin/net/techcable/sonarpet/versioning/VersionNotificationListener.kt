package net.techcable.sonarpet.versioning

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class VersionNotificationListener(): Listener {
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        if (e.player.hasPermission("echopet.version.notify")) {
            PluginVersioning.sendOutdatedVersionNotification(e.player)
        }
    }

    companion object {
        @JvmStatic
        val isNeeded: Boolean
            get() = PluginVersioning.areNotificationsNeeded
    }
}