package net.techcable.sonarpet.nms;

import org.bukkit.Material;

public interface BlockSoundData {
    BlockSoundData WOOD = INMS.getInstance().getBlockSoundData(Material.WOOD);

    float getVolume();

    float getPitch();
}
