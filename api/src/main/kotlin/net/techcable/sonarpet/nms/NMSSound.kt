package net.techcable.sonarpet.nms

import org.bukkit.Sound

/**
 * A NMS sound object,
 * which may or may not have a [bukkit Sound][Sound] counterpart.
 */
interface NMSSound {
    val bukkitSound: Sound?
}
