package net.techcable.sonarpet.nms.versions.v1_11_R1

import net.minecraft.server.v1_11_R1.SoundEffect
import net.techcable.sonarpet.nms.NMSSound
import net.techcable.sonarpet.utils.buildImmutableMap
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_11_R1.CraftSound

private val BUKKIT_SOUNDS = buildImmutableMap<SoundEffect, Sound> {
    for (sound in enumValues<Sound>()) {
        put(sound.soundEffect, sound)
    }
}

val Sound.soundEffect: SoundEffect
    get() = CraftSound.getSoundEffect(CraftSound.getSound(this))!!

class NMSSoundImpl(val handle: SoundEffect): NMSSound {
    override val bukkitSound: Sound?
        get() = BUKKIT_SOUNDS[handle]
}