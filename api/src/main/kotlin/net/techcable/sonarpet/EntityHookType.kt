package net.techcable.sonarpet

import com.dsh105.echopet.compat.api.entity.PetType
import net.techcable.sonarpet.nms.INMS
import net.techcable.sonarpet.utils.NmsVersion
import net.techcable.sonarpet.utils.Versioning
import net.techcable.sonarpet.utils.reflection.MinecraftReflection

enum class EntityHookType(
        val nmsName: String,
        val petType: PetType,
        val earliestVersion: NmsVersion = NmsVersion.EARLIEST,
        val latestVersion: NmsVersion = NmsVersion.LATEST
) {
    BAT("Bat", PetType.BAT),
    BLAZE("Blaze", PetType.BLAZE),
    CAVE_SPIDER("CaveSpider", PetType.CAVESPIDER),
    CHICKEN("Chicken", PetType.CHICKEN),
    COW("Cow", PetType.COW),
    CREEPER("Creeper", PetType.CREEPER),
    ENDER_DRAGON("EnderDragon", PetType.ENDERDRAGON),
    ENDERMAN("Enderman", PetType.ENDERMAN),
    ENDERMITE("Endermite", PetType.ENDERMITE),
    GHAST("Ghast", PetType.GHAST),
    GUARDIAN("Guardian", PetType.GUARDIAN),
    // As of 1.11, elder guardian is a separate entity type
    ELDER_GUARDIAN("GuardianElder", PetType.GUARDIAN, earliestVersion = NmsVersion.v1_11_R1),
    // Horses were split up into multiple different entity types as of 1.11
    HORSE("Horse", PetType.HORSE),
    MULE("HorseMule", PetType.HORSE, earliestVersion = NmsVersion.v1_11_R1),
    DONKEY("HorseDonkey", PetType.HORSE, earliestVersion = NmsVersion.v1_11_R1),
    ZOMBIE_HORSE("HorseZombie", PetType.HORSE, earliestVersion = NmsVersion.v1_11_R1),
    SKELETON_HORSE("HorseSkeleton", PetType.HORSE, earliestVersion = NmsVersion.v1_11_R1),
    IRON_GOLEM("IronGolem", PetType.IRONGOLEM),
    MAGMA_CUBE("MagmaCube", PetType.MAGMACUBE),
    MUSHROOM_COW("MushroomCow", PetType.MUSHROOMCOW),
    OCELOT("Ocelot", PetType.OCELOT),
    PIG("Pig", PetType.PIG),
    PIG_ZOMBIE("PigZombie", PetType.PIGZOMBIE),
    RABBIT("Rabbit", PetType.RABBIT),
    SHEEP("Sheep", PetType.SHEEP),
    SILVERFISH("Silverfish", PetType.SILVERFISH),
    SKELETON("Skeleton", PetType.SKELETON),
    STRAY_SKELETON("SkeletonStray", PetType.SKELETON),
    WITHER_SKELETON("SkeletonWither", PetType.SKELETON),
    SLIME("Slime", PetType.SLIME),
    SNOWMAN("Snowman", PetType.SNOWMAN),
    SPIDER("Spider", PetType.SPIDER),
    SQUID("Squid", PetType.SQUID),
    VILLAGER("Villager", PetType.VILLAGER),
    WITCH("Witch", PetType.WITCH),
    WITHER("Wither", PetType.WITHER),
    WOLF("Wolf", PetType.WOLF),
    // Zombies were split up into different entity types as of 1.11
    ZOMBIE("Zombie", PetType.ZOMBIE, latestVersion = NmsVersion.v1_10_R1),
    HUSK_ZOMBIE("ZombieHusk", PetType.ZOMBIE, earliestVersion = NmsVersion.v1_11_R1),
    VILLAGER_ZOMBIE("ZombieVillager", PetType.ZOMBIE, earliestVersion = NmsVersion.v1_11_R1),
    GIANT_ZOMBIE("GiantZombie", PetType.ZOMBIE);

    val isActive = Versioning.NMS_VERSION in earliestVersion..latestVersion
    val nmsType: Class<*>
        get() {
            check(isActive)
            return MinecraftReflection.getNmsClass("Entity$nmsName") ?: throw IllegalStateException("Unknown NMS name '$nmsName' on version ${Versioning.NMS_VERSION}")
        }
    val entityId: Int
        get() {
            check(isActive)
            return INMS.getInstance().entityRegistry.getEntityId(nmsType)
        }
    val entityName: String
        get() {
            check(isActive)
            return INMS.getInstance().entityRegistry.getEntityName(nmsType)
        }
}