/*
 * This file is part of EchoPet.
 *
 * EchoPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EchoPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EchoPet.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.echopet.compat.api.config;

import com.dsh105.echopet.compat.api.entity.PetType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SpawnEgg;

public enum PetItem {

    BAT(PetType.BAT, Material.MONSTER_EGG, new SpawnEgg(EntityType.BAT), "Bat Pet", "bat"),
    BLAZE(PetType.BLAZE, Material.MONSTER_EGG, new SpawnEgg(EntityType.BLAZE), "Blaze Pet", "blaze"),
    CAVESPIDER(PetType.CAVESPIDER, Material.MONSTER_EGG, new SpawnEgg(EntityType.CAVE_SPIDER), "Cave Spider Pet", "cavespider"),
    CHICKEN(PetType.CHICKEN, Material.MONSTER_EGG, new SpawnEgg(EntityType.CHICKEN), "Chicken Pet", "chicken"),
    COW(PetType.COW, Material.MONSTER_EGG, new SpawnEgg(EntityType.COW), "Cow Pet", "cow"),
    CREEPER(PetType.CREEPER, Material.MONSTER_EGG, new SpawnEgg(EntityType.CREEPER), "Creeper Pet", "creeper"),
    ENDERDRAGON(PetType.ENDERDRAGON, Material.DRAGON_EGG, null, "EnderDragon Pet", "enderdragon"),
    ENDERMAN(PetType.ENDERMAN, Material.MONSTER_EGG, new SpawnEgg(EntityType.ENDERMAN), "Enderman Pet", "enderman"),
    ENDERMITE(PetType.ENDERMITE, Material.MONSTER_EGG, new SpawnEgg(EntityType.ENDERMITE), "Endermite Pet", "endermite"),
    GHAST(PetType.GHAST, Material.MONSTER_EGG, new SpawnEgg(EntityType.GHAST), "Ghast Pet", "ghast"),
    GIANT(PetType.GIANT, Material.MONSTER_EGG, new SpawnEgg(EntityType.GIANT), "Giant Pet", "giant"),
    GUARDIAN(PetType.GUARDIAN, Material.MONSTER_EGG, new SpawnEgg(EntityType.GUARDIAN), "Guardian Pet", "guardian"),
    HORSE(PetType.HORSE, Material.MONSTER_EGG, new SpawnEgg(EntityType.HORSE), "Horse Pet", "horse"),
    HUMAN(PetType.HUMAN, Material.SKULL_ITEM, new MaterialData(Material.SKULL_ITEM, (byte) 3), "Human Pet", "human"),
    IRONGOLEM(PetType.IRONGOLEM, Material.PUMPKIN, null, "Iron Golem Pet", "irongolem"),
    MAGMACUBE(PetType.MAGMACUBE, Material.MONSTER_EGG, new SpawnEgg(EntityType.MAGMA_CUBE), "Magma Cube Pet", "magmacube"),
    MUSHROOMCOW(PetType.MUSHROOMCOW, Material.MONSTER_EGG, new SpawnEgg(EntityType.MUSHROOM_COW), "Mushroom Cow Pet", "mushroomcow"),
    OCELOT(PetType.OCELOT, Material.MONSTER_EGG, new SpawnEgg(EntityType.OCELOT), "Ocelot Pet", "ocelot"),
    PIG(PetType.PIG, Material.MONSTER_EGG, new SpawnEgg(EntityType.PIG), "Pig Pet", "pig"),
    PIGZOMBIE(PetType.PIGZOMBIE, Material.MONSTER_EGG, new SpawnEgg(EntityType.PIG_ZOMBIE), "PigZombie Pet", "pigzombie"),
    RABBIT(PetType.RABBIT, Material.MONSTER_EGG, new SpawnEgg(EntityType.RABBIT), "Rabbit Pet", "rabbit"),
    SHEEP(PetType.SHEEP, Material.MONSTER_EGG, new SpawnEgg(EntityType.SHEEP), "Sheep Pet", "sheep"),
    SILVERFISH(PetType.SILVERFISH, Material.MONSTER_EGG, new SpawnEgg(EntityType.SILVERFISH), "Silverfish Pet", "silverfish"),
    SKELETON(PetType.SKELETON, Material.MONSTER_EGG, new SpawnEgg(EntityType.SKELETON), "Skeleton Pet", "skeleton"),
    SLIME(PetType.SLIME, Material.MONSTER_EGG, new SpawnEgg(EntityType.SLIME), "Slime Pet", "slime"),
    SNOWMAN(PetType.SNOWMAN, Material.SNOW_BALL, null, "Snowman Pet", "snowman"),
    SPIDER(PetType.SPIDER, Material.MONSTER_EGG, new SpawnEgg(EntityType.SPIDER), "Spider Pet", "spider"),
    SQUID(PetType.SQUID, Material.MONSTER_EGG, new SpawnEgg(EntityType.SQUID), "Squid Pet", "squid"),
    VILLAGER(PetType.VILLAGER, Material.MONSTER_EGG, new SpawnEgg(EntityType.VILLAGER), "Villager Pet", "villager"),
    WITCH(PetType.WITCH, Material.MONSTER_EGG, new SpawnEgg(EntityType.WITCH), "Witch Pet", "witch"),
    WITHER(PetType.WITHER, Material.NETHER_STAR, null, "Wither Pet", "wither"),
    WOLF(PetType.WOLF, Material.MONSTER_EGG, new SpawnEgg(EntityType.WOLF), "Wolf Pet", "wolf"),
    ZOMBIE(PetType.ZOMBIE, Material.MONSTER_EGG, new SpawnEgg(EntityType.ZOMBIE), "Zombie Pet", "zombie");

    private String cmd;
    public PetType petType;
    private Material mat;
    private MaterialData data;
    private String name;

    PetItem(PetType petType, Material mat, MaterialData data, String name, String cmd) {
        this.cmd = "pet " + cmd;
        this.petType = petType;
        this.mat = mat;
        this.data = data == null ? new MaterialData(mat) : data;
        this.name = name;
    }

    public String getCommand() {
        return cmd;
    }

    public PetType getPetType() {
        return petType;
    }

    public Material getMat() {
        return mat;
    }

    public short getData() {
        return data.getData();
    }

    public String getName() {
        return name;
    }
}
