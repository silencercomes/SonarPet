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

package com.dsh105.echopet.compat.api.entity

import com.dsh105.echopet.compat.api.plugin.EchoPet
import com.dsh105.echopet.compat.api.util.wrapper.WrappedEntityType
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import lombok.SneakyThrows
import net.techcable.pineapple.reflection.Reflection
import net.techcable.sonarpet.CancelledSpawnException
import net.techcable.sonarpet.EntityHookType
import net.techcable.sonarpet.utils.PrettyEnum
import net.techcable.sonarpet.utils.Versioning
import net.techcable.sonarpet.utils.reflection.MinecraftReflection
import net.techcable.sonarpet.utils.requireNoDuplicates
import net.techcable.sonarpet.utils.toImmutableList
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.lang.invoke.MethodHandle
import java.util.*

enum class PetType(
        private val classIdentifier: String,
        val registrationId: Int,
        val defaultName: String,
        val maxHealth: Double,
        private val attackDamage: Double,
        entityTypeName: String,
        allowedData: ImmutableSet<PetData>
) : PrettyEnum {

    // Aggressive mobs
    BLAZE("Blaze", 61, "Blaze Pet", 20.0, 6.0, "BLAZE", PetData.FIRE),
    CAVESPIDER("CaveSpider", 59, "Cave Spider Pet", 12.0, 5.0, "CAVE_SPIDER"),
    CREEPER("Creeper", 50, "Creeper Pet", 20.0, 6.0, "CREEPER", PetData.POWER),
    ENDERDRAGON("EnderDragon", 63, "EnderDragon Pet", 200.0, 0.0, "ENDER_DRAGON"),
    ENDERMAN("Enderman", 58, "Enderman Pet", 40.0, 6.0, "ENDERMAN", PetData.SCREAMING),
    ENDERMITE("Endermite", 67, "Endermite Pet", 2.0, 2.0, "ENDERMITE"),
    GHAST("Ghast", 56, "Ghast Pet", 10.0, 7.0, "GHAST"),
    GIANT("Giant", 53, "Giant Pet", 100.0, 0.0, "GIANT") {
        override val nmsClass: Class<*>
            get() = MinecraftReflection.findNmsClass("EntityGiantZombie")
    },
    GUARDIAN("Guardian", 68, "Guardian Pet", 20.0, 10.0, "GUARDIAN", PetData.ELDER),
    MAGMACUBE("MagmaCube", 62, "Magma Cube Pet", 20.0, 5.0, "MAGMA_CUBE", PetData.Type.SIZE.values),
    SILVERFISH("Silverfish", 60, "Silverfish Pet", 8.0, 4.0, "SILVERFISH"),
    SKELETON("Skeleton", 51, "Skeleton Pet", 20.0, 5.0, "SKELETON", PetData.WITHER),
    SLIME("Slime", 55, "Slime Pet", 20.0, 4.0, "SLIME", PetData.Type.SIZE.values),
    SPIDER("Spider", 52, "Spider Pet", 16.0, 5.0, "SPIDER"),
    WITCH("Witch", 66, "Witch Pet", 26.0, 5.0, "WITCH"),
    WITHER("Wither", 64, "Wither Pet", 300.0, 8.0, "WITHER", PetData.SHIELD),
    ZOMBIE("Zombie", 54, "Zombie Pet", 20.0, 5.0, "ZOMBIE", PetData.BABY, *PetData.Type.ZOMBIE_TYPE.valueArray),

    // Passive mobs
    BAT("Bat", 65, "Bat Pet", 6.0, 3.0, "BAT"),
    CHICKEN("Chicken", 93, "Chicken Pet", 4.0, 3.0, "CHICKEN", PetData.BABY),
    COW("Cow", 92, "Cow Pet", 10.0, 4.0, "COW", PetData.BABY),
    HORSE("Horse",
            100,
            "Horse Pet",
            30.0,
            4.0,
            "HORSE",
            PetData.BABY,
            PetData.CHESTED,
            PetData.SADDLE,
            *PetData.Type.HORSE_TYPE.valueArray,
            *PetData.Type.HORSE_VARIANT.valueArray,
            *PetData.Type.HORSE_MARKING.valueArray,
            *PetData.Type.HORSE_ARMOUR.valueArray
    ),
    IRONGOLEM("IronGolem", 99, "Iron Golem Pet", 100.0, 7.0, "IRON_GOLEM"),
    MUSHROOMCOW("MushroomCow", 96, "Mushroom Cow Pet", 10.0, 3.0, "MUSHROOM_COW", PetData.BABY),
    OCELOT("Ocelot", 98, "Ocelot Pet", 10.0, 4.0, "OCELOT", PetData.BABY, *PetData.Type.CAT.valueArray),
    PIG("Pig", 90, "Pig Pet", 10.0, 3.0, "PIG", PetData.BABY, PetData.SADDLE),
    RABBIT("Rabbit", 101, "Rabbit Pet", 8.0, 3.0, "RABBIT", PetData.BABY, *PetData.Type.RABBIT_TYPE.valueArray),
    SHEEP("Sheep", 91, "Sheep Pet", 8.0, 3.0, "SHEEP", PetData.BABY, PetData.SHEARED, *PetData.Type.COLOUR.valueArray),
    SNOWMAN("Snowman", 97, "Snowman Pet", 4.0, 4.0, "SNOWMAN"),
    SQUID("Squid", 94, "Squid Pet", 10.0, 4.0, "SQUID"),
    VILLAGER("Villager", 120, "Villager Pet", 20.0, 4.0, "VILLAGER", PetData.BABY, *PetData.Type.PROF.valueArray),
    WOLF("Wolf", 95, "Wolf Pet", 20.0, 6.0, "WOLF", PetData.BABY, PetData.TAMED, PetData.ANGRY, *PetData.Type.COLOUR.valueArray),
    HUMAN("Human", 54, "Human Pet", 20.0, 6.0, "UNKNOWN"),
    PARROT("Parrot", 105, "Parrot Pet", 6.0, 4.0, "PARROT", PetData.BABY, *PetData.Type.PARROT_COLOR.valueArray);

    val petClass: Class<out IPet> = Class.forName("com.dsh105.echopet.api.pet.type." + classIdentifier + "Pet").asSubclass(IPet::class.java)
    private val petConstructor: MethodHandle
    private val entityTypeWrapper: WrappedEntityType
    val allowedDataTypes: ImmutableSet<PetData>

    constructor(
            classIdentifier: String,
            registrationId: Int,
            defaultName: String,
            maxHealth: Double,
            attackDamage: Double,
            entityTypeName: String,
            vararg data: PetData
    ) : this(classIdentifier, registrationId, defaultName, maxHealth, attackDamage, entityTypeName, ImmutableSet.copyOf(data)) {
        data.requireNoDuplicates() // There's no reason to ever have a duplicate, so treat it as a bug
    }

    init {
        this.petConstructor = Reflection.getConstructor(petClass, Player::class.java)
        this.allowedDataTypes = ImmutableSet.copyOf(allowedData)
        this.entityTypeWrapper = WrappedEntityType(entityTypeName)
    }

    fun getDefaultName(name: String): String {
        return EchoPet.getConfig().getString("pets." + this.toString().toLowerCase().replace("_", " ") + ".defaultName", this.defaultName).replace("(user)", name).replace("(userApos)", name + "'s")
    }

    fun getAttackDamage(): Double {
        return EchoPet.getConfig().getDouble("pets." + this.toString().toLowerCase().replace("_", " ") + ".attackDamage", this.attackDamage)
    }

    val entityType: EntityType
        get() = this.entityTypeWrapper.get()

    fun isDataAllowed(data: PetData): Boolean {
        return allowedDataTypes.contains(data)
    }

    @Throws(CancelledSpawnException::class)
    fun getNewPetInstance(owner: Player): IPet {
        require(isSupported) { "$this is not supported on ${Versioning.NMS_VERSION}" }
        return petConstructor.invokeWithArguments(owner) as IPet
    }

    open val nmsClass: Class<*>
        get() = MinecraftReflection.findNmsClass("Entity$classIdentifier")
    // NOTE: Lazy-load hookType information to avoid circular dependency issues
    val hookTypes: ImmutableList<EntityHookType> by lazy {
        EntityHookType.values()
                .filter { it.petType == this@PetType && it.isActive }
                .toImmutableList()
    }
    val primaryHookType: EntityHookType? by lazy {
        if (hookTypes.isEmpty()) null else hookTypes.single { it.nmsName == classIdentifier }
    }

    val isSupported: Boolean
        get() = hookTypes.isNotEmpty()

    companion object {

        /**
         * Get the pet type for the specified name, properly converting legacy pet types into their new representation.
         *
         * This should be preferred to [valueOf] when loading from storage, since it handles legacy formats.
         * However, [valueOf] should be preferred for the user interface as it enforces the modern representation.
         *
         * @param petTypeName the name of the pet's type
         * @param petData the list of pet data, to add attributes to if needed.
         * @return the pet's data type
         */
        @JvmStatic
        fun fromDataString(petTypeName: String, petData: MutableList<PetData>): PetType {
            Objects.requireNonNull(petTypeName, "Null petTypeName!")
            Objects.requireNonNull<List<PetData>>(petData, "Null petData!")
            return when (petTypeName) {
                "PIGZOMBIE" -> { // Convert the old pigman type to a zombie with a pigman attribute
                    petData.add(PetData.PIGMAN)
                    PetType.ZOMBIE
                }
                else -> {
                    try {
                        PetType.valueOf(petTypeName)
                    } catch (ignored: IllegalArgumentException) {
                        throw IllegalArgumentException("Unknown petType: " + petTypeName)
                    }
                }
            }
        }
    }
}