@file:Suppress("INTERFACE_STATIC_METHOD_CALL_FROM_JAVA6_TARGET")

package net.techcable.sonarpet

import com.dsh105.echopet.compat.api.entity.IEntityPet
import com.dsh105.echopet.compat.api.entity.IPet
import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin
import net.techcable.pineapple.reflection.PineappleField
import net.techcable.sonarpet.nms.INMS
import net.techcable.sonarpet.nms.NMSInsentientEntity
import net.techcable.sonarpet.nms.entity.generators.EntityPetGenerator
import net.techcable.sonarpet.nms.entity.generators.GeneratorClass
import net.techcable.sonarpet.utils.Versioning
import net.techcable.sonarpet.utils.reflection.MinecraftReflection
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.objectweb.asm.Type
import java.util.*

class HookRegistryImpl(private val plugin: IEchoPetPlugin) : HookRegistry {
    private val registeredHooks: MutableMap<EntityHookType, HookRegistrationInfo> = EnumMap(EntityHookType::class.java)
    override fun registerHookClass(type: EntityHookType, hookClass: Class<out IEntityPet>) {
        require(type.isActive) {
            "HookType $type isn't active on ${Versioning.NMS_VERSION}"
        }
        if (type in registeredHooks) {
            throw IllegalArgumentException("Can't register hook class $hookClass for $type, as it's already registered to ${registeredHooks[type]!!.hookClass}")
        }
        val entityRegistry = INMS.getInstance().entityRegistry
        val entityClass = generateEntityClass(type, hookClass)
        val hookInfo = HookRegistrationInfo(type, hookClass, entityClass)
        registeredHooks.put(type, hookInfo)
        entityRegistry.registerEntityClass(
                entityClass,
                hookInfo.name,
                hookInfo.id
        )
    }

    override fun shutdown() {
        val entityRegistry = INMS.getInstance().entityRegistry
        registeredHooks.forEach { _, hookInfo ->
            entityRegistry.unregisterEntityClass(
                    hookInfo.entityClass,
                    hookInfo.name,
                    hookInfo.id
            )
        }
        registeredHooks.clear()
    }

    override fun spawnEntity(pet: IPet, hookType: EntityHookType, location: Location): IEntityPet {
        val hookInfo = registeredHooks[hookType] ?: throw IllegalArgumentException("Unknown class for hook type $hookType")
        val entity = hookInfo.createEntity(location.world)
        val nmsEntity = INMS.getInstance().wrapEntity(entity) as NMSInsentientEntity
        val hook = hookInfo.createHook(pet, nmsEntity)
        hookInfo.injectHook(entity, hook)
        INMS.getInstance().spawnEntity(nmsEntity, location)
        hook.initiateEntityPet()
        return hook
    }

    private fun generateEntityClass(type: EntityHookType, hookClass: Class<out IEntityPet>): Class<*> {
        val generatedType = Type.getObjectType("net/techcable/sonarpet/nms/entities/type/Generated" +  hookClass.simpleName)
        val generatorClass: Class<out EntityPetGenerator>
        if (hookClass.isAnnotationPresent(GeneratorClass::class.java)) {
            generatorClass = hookClass.getAnnotation(GeneratorClass::class.java).value.java
        } else {
            generatorClass = EntityPetGenerator::class.java
        }
        try {
            return generatorClass.getConstructor(IEchoPetPlugin::class.java, Type::class.java, Class::class.java, Class::class.java)
                    .newInstance(plugin, generatedType, hookClass, type.nmsType)
                    .generateClass()
        } catch (e: ReflectiveOperationException) {
            throw RuntimeException("Unable to generate class for " + type, e)
        }
    }
}

private class HookRegistrationInfo(
        val hookType: EntityHookType,
        val hookClass: Class<out IEntityPet>,
        val entityClass: Class<*>
) {
    private val hookConstructor = hookClass.getDeclaredConstructor(IPet::class, NMSInsentientEntity::class, EntityHookType::class).apply {
        isAccessible = true
    }
    private val entityConstructor = entityClass.getDeclaredConstructor(MinecraftReflection.getNmsClass("World")).apply {
        isAccessible = true
    }
    private val entityHookField = PineappleField.create(entityClass, "hook", IEntityPet::class.java)
    fun createEntity(world: World): Entity {
        val worldHandle = WORLD_GET_HANDLE_METHOD(world)
        val rawEntity = entityConstructor(worldHandle)
        return ENTITY_GET_BUKKIT_ENTITY_METHOD(rawEntity) as Entity
    }
    fun createHook(pet: IPet, entity: NMSInsentientEntity): IEntityPet {
        return hookConstructor(pet, entity, hookType)
    }
    fun injectHook(entity: Entity, hook: IEntityPet) {
        val rawEntity = MinecraftReflection.getHandle(entity)
        entityHookField.put(rawEntity, hook) // Inject the hook
    }
    val id = INMS.getInstance().entityRegistry.getEntityId(hookType.nmsType)
    val name = "Sonar${INMS.getInstance().entityRegistry.getEntityName(hookType.nmsType)!!}"
}

// Reflection
private val WORLD_GET_HANDLE_METHOD = MinecraftReflection.getObcClass("CraftWorld").getDeclaredMethod("getHandle")!!.apply {
    isAccessible = true
}
private val ENTITY_GET_BUKKIT_ENTITY_METHOD = MinecraftReflection.getNmsClass("Entity").getDeclaredMethod("getBukkitEntity").apply {
    isAccessible = true
}
