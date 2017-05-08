package net.techcable.sonarpet.nms.versions.v1_8_R3

import net.techcable.sonarpet.nms.NMSSound
import net.techcable.sonarpet.utils.buildImmutableMap
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_8_R3.CraftSound

private val BUKKIT_SOUNDS = buildImmutableMap<String, Sound> {
    for (sound in enumValues<Sound>()) {
        put(sound.internalName, sound)
    }
}

val Sound.internalName: String
    get() = CraftSound.getSound(this)!!

class NMSSoundImpl(val internalName: String): NMSSound {
    override val bukkitSound: Sound?
        get() = BUKKIT_SOUNDS[internalName]
}