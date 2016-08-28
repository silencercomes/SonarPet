//
// Field name definitions
//

#if NMS_VERSION == v1_10_R1
    #define IS_JUMPING_FIELD_NAME be
#elif NMS_VERSION == v1_9_R2
    #define IS_JUMPING_FIELD_NAME bd
#elif NMS_VERSION == v1_9_R1
    #define IS_JUMPING_FIELD_NAME bc
#elif NMS_VERSION == v1_8_R3
    #define IS_JUMPING_FIELD_NAME aY
#else
    #error "Unkown entity step height field for version NMS_VERSION"
#endif

#if NMS_VERSION == v1_10_R1 || NMS_VERSION == v1_9_R2 || NMS_VERSION == v1_9_R1
    #define STEP_HEIGHT P
#elif NMS_VERSION == v1_8_R3
    #define STEP_HEIGHT S
#else
    #error "Unknown entity step height field for version NMS_VERSION"
#endif

//
// Method name definitions
//

#if NMS_VERSION == v1_10_R1 || NMS_VERSION == v1_9_R2 || NMS_VERSION == v1_9_R1
    #define ENTITY_MOVE_METHOD g
    #define ENTITY_TICK_METHOD m
#elif NMS_VERSION == v1_8_R3
    #define ENTITY_MOVE_METHOD g
    #define ENTITY_TICK_METHOD t_
#else
    #error "Unknown entity move and tick methods for version NMS_VERSION"
#endif

#if NMS_VERSION == v1_10_R1 || NMS_VERSION == v1_9_R2 || NMS_VERSION == v1_9_R1 || NMS_VERSION == v1_8_R3
    #define ON_STEP_METHOD a
#else
    #error "Unkown onStep method for version NMS_VERSION"
#endif

//
// Marcos
//

// NOTE: marcos have no type safety, so we insert casts to try and get compiler warnings when casts are used on inproper types

/**
 * Set the 'offsets' for pitch and yaw to the same value as the yaw itself.
 * Apparently this is needed to set rotation.
 * See EntityLiving.h(FF) for details (method profiler 'headTurn').
 * Note that EntityInsentient overrides h(FF) and delegates to 'EntityAIBodyControl'.
 * 'EntityAIBodyControl' is what actually accesses/uses these fields and where the mappings should be fetched.
 *
 * Also, these fields have the MCP names 'renderYawOffset' and 'rotationYawHead'
 */
#if NMS_VERSION == v1_10_R1
    #define CORRECT_YAW(entity) ((EntityInsentient) entity).aO = entity.aQ = entity.yaw;
#elif NMS_VERSION == v1_9_R2
    #define CORRECT_YAW(entity) ((EntityInsentient) entity).aN = entity.aP = entity.yaw;
#elif NMS_VERSION == v1_9_R1
    #define CORRECT_YAW(entity) ((EntityInsentient) entity).aM = entity.aO = entity.yaw;
#elif NMS_VERSION == v1_8_R3
    #define CORRECT_YAW(entity) ((EntityInsentient) entity).aK = entity.aI = entity.yaw;
#else
    #error "Don't know how to CORRECT_YAW(Entity) for version NMS_VERSION"
#endif

// NOTE: we don't cast speed here since casting primitive types will actually supress type errors, not trigger them

#if NMS_VERSION == v1_10_R1 || NMS_VERSION == v1_9_R2 || NMS_VERSION == v1_9_R1
    #define SET_MOVE_SPEED(entity, speed) ((EntityInsentient) entity).l(speed);
#elif NMS_VERSION == v1_8_R3
    #define SET_MOVE_SPEED(entity, speed) ((EntityInsentient) entity).k(speed);
#else
    #error "Don't know how to SET_MOVE_SPEED(Entity, float) for version NMS_VERSION"
#endif

#if NMS_VERSION == v1_10_R1
    #define IS_IN_WATER(entity) entity.isInWater()
    #define IS_IN_LAVA(entity) ((EntityInsentient) entity).ao()
#elif NMS_VERSION == v1_9_R2 || NMS_VERSION == v1_9_R1
    #define IS_IN_WATER(entity) entity.isInWater()
    #define IS_IN_LAVA(entity) ((EntityInsentient) entity).an()
#elif NMS_VERSION == v1_8_R3
    #define IS_IN_WATER(entity) ((EntityInsentient) entity).V()
    #define IS_IN_LAVA(entity) ((EntityInsentient) entity).ab()
#else
    #error "Don't know how to check if an entity is in water or lava for version NMS_VERSION"
#endif

#if NMS_VERSION == v1_10_R1 || NMS_VERSION == v1_9_R2 || NMS_VERSION == v1_9_R1 || NMS_VERSION == v1_8_R3
    #define SET_CAN_SWIM(entity, canSwim) ((Navigation) entity.getNavigation()).c(canSwim)
#else
    #error "Don't know how to set if an entity can swim for version NMS_VERSION"
#endif

#if NMS_VERSION == v1_10_R1 || NMS_VERSION == v1_9_R2 || NMS_VERSION == v1_9_R1
    #define GET_VERTICAL_FACE_SPEED(entity) ((EntityInsentient) entity).N()
#elif NMS_VERSION == v1_8_R3
    #define GET_VERTICAL_FACE_SPEED(entity) ((EntityInsentient) entity).bQ()
#else
    #error "Don't know how to get vertical face speed for version NMS_VERSION"
#endif


#if NMS_VERSION == v1_10_R1 || NMS_VERSION == v1_9_R2 || NMS_VERSION == v1_9_R1 || NMS_VERSION == v1_8_R3
    #define GET_DISTANCE_BETWEEN_ENTITIES(first, second) ((Entity) first).h((Entity) second)
#else
    #error "Don't know how to calculate distance etween entities for version NMS_VERSION"
#endif

#if NMS_VERSION == v1_10_R1 || NMS_VERSION == v1_9_R2 || NMS_VERSION == v1_9_R1
    #define SOUND net.minecraft.server.NMS_VERSION_STRING.SoundEffect
#elif NMS_VERSION == v1_8_R3
    #define SOUND String
#else
    #error "Don't know what the sound class is for this version!"
#endif


#if NMS_VERSION >= v1_9_R1
    import net.minecraft.server.NMS_VERSION_STRING.EnumHorseType;
    #define SET_HORSE_VARIANT(horse, variant) ((EntityHorse) horse).setType(EnumHorseType.values()[((Horse.Variant) variant).ordinal()])
    #define GET_HORSE_VARIANT(horse) (Horse.Variant.values()[((EntityHorse) horse).getType().ordinal()])
#else
    #define SET_HORSE_VARIANT(horse, variant) ((EntityHorse) horse).setType(((Horse.Variant) variant).ordinal())
    #define GET_HORSE_VARIANT(horse) (Horse.Variant.values()[((EntityHorse) horse).getType()])
#endif

#if NMS_VERSION >= v1_9_R1
    import net.minecraft.server.NMS_VERSION_STRING.EnumItemSlot;
    #define ITEM_SLOT_MAINHAND EnumItemSlot.MAINHAND
#else
    #define ITEM_SLOT_MAINHAND 0
#endif

#if NMS_VERSION >= v1_9_R1
    import net.minecraft.server.NMS_VERSION_STRING.DataWatcherObject;
    import net.minecraft.server.NMS_VERSION_STRING.DataWatcherRegistry;
    #define BOOLEAN_METADATA_TYPE DataWatcherRegistry.h
    #define INTEGER_METADATA_TYPE DataWatcherRegistry.b
    #define GET_METADATA_BOOLEAN(entity, id) ((Boolean) ((Entity) entity).getDataWatcher().get(new DataWatcherObject(id, BOOLEAN_METADATA_TYPE)))
    #define SET_METADATA_BOOLEAN(entity, id, value) ((Entity) entity).getDataWatcher().set(new DataWatcherObject(id, BOOLEAN_METADATA_TYPE), value)
    #define SET_METADATA_INTEGER(entity, id, value) ((Entity) entity).getDataWatcher().set(new DataWatcherObject(id, INTEGER_METADATA_TYPE), value)
#else
    #define GET_METADATA_BOOLEAN(entity, id) (((Entity) entity).getDataWatcher().getByte(id) != 0)
    #define SET_METADATA_BOOLEAN(entity, id, value) ((Entity) entity).getDataWatcher().watch(id, ((boolean) value) ? 1 : 0)
    #define SET_METADATA_INTEGER(entity, id, value) ((Entity) entity).getDataWatcher().watch(id, value)
#endif
