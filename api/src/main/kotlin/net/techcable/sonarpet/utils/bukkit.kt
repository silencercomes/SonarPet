package net.techcable.sonarpet.utils

import com.google.common.util.concurrent.AbstractFuture
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.common.util.concurrent.SettableFuture
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

//
// Bukkit utilities
//

fun <T> Plugin.scheduleTask(delay: Long = 0, async: Boolean = false, task: () -> T): ListenableFuture<T> {
    require(delay >= 0) { "Invalid delay: $delay" }
    val future = SettableFuture.create<T>()
    val runnable = object: BukkitRunnable() {
        override fun run() {
            try {
                val result = task()
                future.set(result)
            } catch (e: Throwable) {
                future.setException(e)
                throw e // Continue propagating the exception
            }
        }
    }
    future.addListener {
        if (future.isCancelled) runnable.cancel()
    }
    runnable.schedule(plugin = this, delay = delay, async = async)
    return future
}

fun BukkitRunnable.schedule(plugin: Plugin, delay: Long = 0, async: Boolean = false) {
    if (async) {
        if (delay > 0) {
            this.runTaskLaterAsynchronously(plugin, delay)
        } else {
            this.runTaskAsynchronously(plugin)
        }
    } else {
        if (delay > 0) {
            this.runTaskLater(plugin, delay)
        } else {
            this.runTask(plugin)
        }
    }
}
