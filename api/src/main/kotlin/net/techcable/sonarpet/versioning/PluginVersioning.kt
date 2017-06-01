package net.techcable.sonarpet.versioning

import com.dsh105.echopet.compat.api.plugin.EchoPet
import com.dsh105.echopet.compat.api.util.Perm
import net.techcable.sonarpet.utils.scheduleTask
import org.bukkit.command.CommandSender
import java.io.IOException
import java.util.logging.Level

object PluginVersioning {
    const val BRANCH = "master"
    const val REPO = "TechzoneMC/SonarPet"

    val currentVersion: PluginVersionInfo
        get() {
            val description = EchoPet.getPlugin().description
            return PluginVersionInfo.parse(description.name, description.version)
        }
    var knownDifferenceWithLatest: VersionDifference? = null
            private set
    private val lock = Any()
    @Throws(VersioningException::class, IOException::class)
    fun compareToLatest(): VersionDifference {
        require(currentVersion.isDevelopment) { "$currentVersion isn't a dev build" }
        var difference = this.knownDifferenceWithLatest
        if (difference != null) return difference
        synchronized(lock) {
            difference = this.knownDifferenceWithLatest
            if (difference == null) {
                difference = currentVersion.compareToRepo(repo = REPO, branch = BRANCH)
                this.knownDifferenceWithLatest = difference
            }
            return difference!!
        }
    }

    fun VersionDifference.sendTo(target: CommandSender) {
        val message = StringBuilder("$currentVersion is $this the latest version")
        if (currentVersion.isDirty) {
            message.append(", but has uncommitted custom changes.")
        }
        target.sendMessage(message.toString())
        if (behindBy > 0 || currentVersion.isDirty) {
            target.sendMessage("Please update to the latest development build before reporting bugs with this one.")
        }
    }

    val areNotificationsNeeded: Boolean
        get() = PluginVersioning.currentVersion.isDevelopment
    fun sendOutdatedVersionNotification(target: CommandSender) {
        if (areNotificationsNeeded) {
            PluginVersioning.sendVersionNotification(
                    target = target,
                    notification = {
                        if (!isIdentical || currentVersion.isDirty) {
                            PluginVersioning.apply {
                                sendTo(target)
                            }
                        }
                    }
            )
        }
    }

    @JvmOverloads
    fun sendVersionNotification(target: CommandSender, notification: VersionDifference.() -> Unit, onWait: () -> Unit = {}) {
        require(currentVersion.isDevelopment) { "$currentVersion isn't a dev build" }
        val difference = knownDifferenceWithLatest
        if (difference != null) {
            difference.sendTo(target)
            return
        }
        onWait()
        EchoPet.getPlugin().scheduleTask(async = true) {
            try {
                compareToLatest().apply(notification)
            } catch (e: Throwable) {
                when (e) {
                    is UnknownVersionException -> {
                        target.sendMessage("Unknown version $currentVersion")
                    }
                    is VersioningException -> {
                        target.sendMessage("Error checking version: ${e.message}")
                    }
                    else -> {
                        target.sendMessage("Unexpected error checking version: ${e.javaClass.simpleName}")
                        EchoPet.getPlugin().logger.log(
                                Level.SEVERE,
                                "Unexpected error checking version",
                                e
                        )
                    }
                }
            }
        }
    }

    fun runVersionCommand(target: CommandSender) {
        if (Perm.DISPLAY_VERSION.hasPerm(target, true, true)) {
            when (currentVersion.buildType) {
                PluginVersionInfo.BuildType.RELEASE -> {
                    target.sendMessage("You are running release $currentVersion")
                }
                PluginVersionInfo.BuildType.DEV, PluginVersionInfo.BuildType.DIRTY -> {
                    val message = StringBuilder("You are running dev build $currentVersion")
                    if (currentVersion.isDirty) {
                        message.append(", which has uncommitted custom changes.")
                    }
                    target.sendMessage(message.toString())
                    check(currentVersion.isDevelopment)
                    sendVersionNotification(
                            target,
                            notification = { sendTo(target) },
                            onWait = { target.sendMessage("Checking version, please wait...") }
                    )
                }
                PluginVersionInfo.BuildType.UNKNOWN -> {
                    target.sendMessage("You are running unknown build $currentVersion")
                }
            }
        }
    }
}