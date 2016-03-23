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

package com.dsh105.echopet.compat.nms.v1_9_R1.entity;

import com.dsh105.echopet.compat.api.ai.PetGoalSelector;
import com.dsh105.echopet.compat.api.entity.*;
import com.dsh105.echopet.compat.api.event.PetAttackEvent;
import com.dsh105.echopet.compat.api.event.PetRideJumpEvent;
import com.dsh105.echopet.compat.api.event.PetRideMoveEvent;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.api.util.Logger;
import com.dsh105.echopet.compat.api.util.MenuUtil;
import com.dsh105.echopet.compat.api.util.Perm;
import com.dsh105.echopet.compat.api.util.menu.MenuOption;
import com.dsh105.echopet.compat.api.util.menu.PetMenu;
import com.dsh105.echopet.compat.nms.v1_9_R1.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R1.NMSEntityUtil;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.WrappedDataWatcher;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.ai.PetGoalFloat;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.ai.PetGoalFollowOwner;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.ai.PetGoalLookAtPlayer;
import com.google.common.base.Preconditions;

import net.minecraft.server.v1_9_R1.*;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_9_R1.CraftSound;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import static com.dsh105.echopet.compat.nms.v1_9_R1.NMS.*;

public abstract class EntityPet extends EntityCreature implements IAnimal, IEntityPet {

    protected IPet pet;
    public PetGoalSelector petGoalSelector;
    /**
     * A object field to warn about accessing the datawatcher field
     * @deprecated use getter
     */
    @Deprecated
    public final Object datawatcher = null;
    public WrappedDataWatcher getDatawatcher() {
        return new WrappedDataWatcher(super.datawatcher);
    }

    /**
     * @deprecated use the wrapper
     */
    @Override
    @Deprecated
    public DataWatcher getDataWatcher() {
        return super.getDataWatcher();
    }

    /**
     * The field to check if the entity is jumping.
     * Uses reflection to bypass access checks
     */
    protected static final Field IS_JUMPING_FIELD;

    static {
        try {
            IS_JUMPING_FIELD = EntityLiving.class.getDeclaredField("bc");
            IS_JUMPING_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("Missed Diff for jumping field", e);
        }
    }

    protected double jumpHeight;

    protected float rideSpeed;
    public EntityLiving goalTarget = null;
    public boolean shouldVanish;

    public EntityPet(World world) {
        super(world);
    }

    public EntityPet(World world, IPet pet) {
        super(world);
        this.pet = pet;
        this.initiateEntityPet();
    }

    private void initiateEntityPet() {
        this.resetEntitySize();
        this.fireProof = true;
        this.getBukkitEntity().setMaxHealth(pet.getPetType().getMaxHealth());
        this.setHealth((float) pet.getPetType().getMaxHealth());
        this.jumpHeight = EchoPet.getOptions().getRideJumpHeight(this.getPet().getPetType());
        this.rideSpeed = EchoPet.getOptions().getRideSpeed(this.getPet().getPetType());
        this.setPathfinding();
    }

    public PetType getEntityPetType() {
        EntityPetType entityPetType = this.getClass().getAnnotation(EntityPetType.class);
        if (entityPetType != null) {
            return entityPetType.petType();
        }
        return null;
    }

    @Override
    public void resizeBoundingBox(boolean flag) {
        EntitySize es = this.getClass().getAnnotation(EntitySize.class);
        if (es != null) {
            this.setSize(flag ? (es.width() / 2) : es.width(), flag ? (es.height() / 2) : es.height());
        }
    }

    @Override
    public void resetEntitySize() {
        EntitySize es = this.getClass().getAnnotation(EntitySize.class);
        if (es != null) {
            this.setSize(es.width(), es.height());
        }
    }

    @Override
    public void setEntitySize(float width, float height) {
        this.setSize(width, height);
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    public IPet getPet() {
        return this.pet;
    }

    public Player getPlayerOwner() {
        return pet.getOwner();
    }

    public EntityPlayer getOwner() {
        return NMS.getHandle(getPlayerOwner());
    }

    public Location getLocation() {
        return this.pet.getLocation();
    }

    public void setVelocity(Vector vel) {
        this.motX = vel.getX();
        this.motY = vel.getY();
        this.motZ = vel.getZ();
        this.velocityChanged = true;
    }

    public Random random() {
        return this.random;
    }

    @Override
    public PetGoalSelector getPetGoalSelector() {
        return petGoalSelector;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public void setShouldVanish(boolean flag) {
        this.shouldVanish = flag;
    }

    @Override
    public void setTarget(LivingEntity livingEntity) {
        this.setGoalTarget(((CraftLivingEntity) livingEntity).getHandle());
    }

    @Override
    public LivingEntity getTarget() {
        return (LivingEntity) this.getGoalTarget().getBukkitEntity();
    }

    public boolean attack(Entity entity) {
        return this.attack(entity, (float) this.getPet().getPetType().getAttackDamage());
    }

    public boolean attack(Entity entity, float damage) {
        return this.attack(entity, DamageSource.mobAttack(this), damage);
    }

    public boolean attack(Entity entity, DamageSource damageSource, float damage) {
        PetAttackEvent attackEvent = new PetAttackEvent(this.getPet(), entity.getBukkitEntity(), damage);
        EchoPet.getPlugin().getServer().getPluginManager().callEvent(attackEvent);
        if (!attackEvent.isCancelled()) {
            if (entity instanceof EntityPlayer) {
                if (!(EchoPet.getConfig().getBoolean("canAttackPlayers", false))) {
                    return false;
                }
            }
            return entity.damageEntity(damageSource, (float) attackEvent.getDamage());
        }
        return false;
    }

    public void setPathfinding() {
        try {
            NMSEntityUtil.clearGoals(this);
            this.petGoalSelector = new PetGoalSelector();

            petGoalSelector.addGoal(new PetGoalFloat(this), 0);
            petGoalSelector.addGoal(new PetGoalFollowOwner(this, this.getSizeCategory().getStartWalk(getPet().getPetType()), this.getSizeCategory().getStopWalk(getPet().getPetType()), this.getSizeCategory().getTeleport(getPet().getPetType())), 1);
            petGoalSelector.addGoal(new PetGoalLookAtPlayer(this, EntityHuman.class), 2);

        } catch (Exception e) {
            Logger.log(Logger.LogLevel.WARNING, "Could not add PetGoals to Pet AI.", e, true);
        }
    }

    @Override
    public CraftCreature getBukkitEntity() {
        return (CraftCreature) super.getBukkitEntity();
    }

    // well then...it's now 'final'

    /*
    // Overriden from EntityInsentient - Most importantly overrides pathfinding selectors
    @Override
    protected final void doTick() {
        super.doTick();
        ++this.ticksFarFromPlayer;

        this.D();

        this.getEntitySenses().a();

        // If this ever happens...
        if (this.petGoalSelector == null) {
            this.remove(false);
            return;
        }
        this.petGoalSelector.updateGoals();

        this.navigation.k();

        this.E();

        this.getControllerMove().c();

        this.getControllerLook().a();

        this.getControllerJump().b();
    }
    */

    @Override
    public boolean onInteract(Player p) {
        if (p.getUniqueId().equals(getPlayerOwner().getUniqueId())) {
            if (EchoPet.getConfig().getBoolean("pets." + this.getPet().getPetType().toString().toLowerCase().replace("_", " ") + ".interactMenu", true) && Perm.BASE_MENU.hasPerm(this.getPlayerOwner(), false, false)) {
                ArrayList<MenuOption> options = MenuUtil.createOptionList(getPet().getPetType());
                int size = this.getPet().getPetType() == PetType.HORSE ? 18 : 9;
                PetMenu menu = new PetMenu(getPet(), options, size);
                menu.open(false);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean a(EntityHuman human) {
        return onInteract((Player) human.getBukkitEntity());
    }

    @Override
    public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
        super.setPositionRotation(d0, d1, d2, f, f1);
    }

    public void setLocation(Location l) {
        Preconditions.checkArgument(l.getWorld().equals(this.getWorld().getWorld()), "Location world %s doesen't match entity world %s", l.getWorld(), this.getWorld());
        this.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
    }

    public void teleport(Location l) {
        this.getPet().getCraftPet().teleport(l);
    }

    @Override
    public void remove(boolean makeSound) {
        if (this.bukkitEntity != null) {
            bukkitEntity.remove();
        }
        if (makeSound) {
            playSound(getDeathSound(), 1.0F, 1.0F);
        }
    }

    public void playSound(Sound bukkitSound, float volume, float pitch) {// A Happy Utility :D
        SoundEffect mojangSound = CraftSound.getSoundEffect(CraftSound.getSound(bukkitSound));
        NMS.playSound(this, mojangSound, volume, pitch);
    }

    public void onLive() {
        if (this.pet == null) {
            this.remove(false);
            return;
        }

        if (this.getPlayerOwner() == null || !this.getPlayerOwner().isOnline()) {
            EchoPet.getManager().removePet(this.getPet(), true);
            return;
        }

        if (pet.isOwnerRiding() && this.passengers.isEmpty() && !pet.isOwnerInMountingProcess()) {
            pet.ownerRidePet(false);
        }

        if (((CraftPlayer) this.getPlayerOwner()).getHandle().isInvisible() != this.isInvisible() && !this.shouldVanish) {
            this.setInvisible(!this.isInvisible());
        }

        if (((CraftPlayer) this.getPlayerOwner()).getHandle().isSneaking() != this.isSneaking()) {
            this.setSneaking(!this.isSneaking());
        }

        if (((CraftPlayer) this.getPlayerOwner()).getHandle().isSprinting() != this.isSprinting()) {
            this.setSprinting(!this.isSprinting());
        }

        if (this.getPet().isHat()) {
            this.lastYaw = this.yaw = (this.getPet().getPetType() == PetType.ENDERDRAGON ? this.getPlayerOwner().getLocation().getYaw() - 180 : this.getPlayerOwner().getLocation().getYaw());
        }

        if (this.getPlayerOwner().isFlying() && EchoPet.getOptions().canFly(this.getPet().getPetType())) {
            Location petLoc = this.getLocation();
            Location ownerLoc = this.getPlayerOwner().getLocation();
            Vector v = ownerLoc.toVector().subtract(petLoc.toVector());

            double x = v.getX();
            double y = v.getY();
            double z = v.getZ();

            Vector vo = this.getPlayerOwner().getLocation().getDirection();
            if (vo.getX() > 0) {
                x -= 1.5;
            } else if (vo.getX() < 0) {
                x += 1.5;
            }
            if (vo.getZ() > 0) {
                z -= 1.5;
            } else if (vo.getZ() < 0) {
                z += 1.5;
            }

            this.setVelocity(new Vector(x, y, z).normalize().multiply(0.3F));
        }
    }

    /**
     * Return if the pet's owner is currently riding the pet
     *
     * @return if the owner is riding
     */
    public boolean isOwnerRiding() {
        for (Entity passenger : this.passengers) {
            if (passenger == getOwner()) {
                return true;
            }
        }
        return false;
    }

    // EntityLiving
    /*
     * We need to override the move logic for special handling when the owner is riding
     */
    @Override
    public void g(float sideMot, float forwMot) {
        if (!isOwnerRiding()) {
            super.g(sideMot, forwMot); // moveEntity
            this.P = 0.5F; // set the step hight to half a blog, like mobs
            return;
        }

        this.P = 1.0F; // Grant the pet a step height of a full block since they have a player riding them

        this.lastYaw = this.yaw = getOwner().yaw;
        this.pitch = getOwner().pitch * 0.5F;
        this.setYawPitch(this.yaw, this.pitch);
        /**
         * Set the 'offsets' for pitch and yaw to the same value as the yaw itself.
         * Apparently this is needed to set rotation.
         * See EntityLiving.h(FF) for details (method profiler 'headTurn')
         */
        this.aM = this.aG = this.yaw;

        sideMot = getSidewaysMotion(getOwner()) * 0.5F;
        forwMot = getForwardsMotion(getOwner());

        if (forwMot <= 0.0F) {
            forwMot *= 0.25F; // quarter speed backwards
        }
        sideMot *= 0.75F; // 75% slower sideways

        PetRideMoveEvent moveEvent = new PetRideMoveEvent(this.getPet(), forwMot, sideMot);
        EchoPet.getPlugin().getServer().getPluginManager().callEvent(moveEvent);
        if (moveEvent.isCancelled()) {
            return;
        }

        this.k(this.rideSpeed); // set the movement speed
        super.g(moveEvent.getSidewardMotionSpeed(), moveEvent.getForwardMotionSpeed()); // superclass movement logic, with the speed from the movement event

        PetType pt = this.getPet().getPetType();
        if (IS_JUMPING_FIELD != null) {
            if (EchoPet.getOptions().canFly(pt)) {
                try {
                    if (getOwner().getBukkitEntity().isFlying()) {
                        getOwner().getBukkitEntity().setFlying(false);
                    }
                    if (IS_JUMPING_FIELD.getBoolean(getOwner())) {
                        PetRideJumpEvent rideEvent = new PetRideJumpEvent(this.getPet(), this.jumpHeight);
                        EchoPet.getPlugin().getServer().getPluginManager().callEvent(rideEvent);
                        if (!rideEvent.isCancelled()) {
                            this.motY = 0.5F;
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException | IllegalStateException e) {
                    Logger.log(Logger.LogLevel.WARNING, "Failed to initiate Pet Flying Motion for " + this.getPlayerOwner().getName() + "'s Pet.", e, true);
                }
            } else if (this.onGround) {
                try {
                    if (IS_JUMPING_FIELD.getBoolean(getOwner())) {
                        PetRideJumpEvent rideEvent = new PetRideJumpEvent(this.getPet(), this.jumpHeight);
                        EchoPet.getPlugin().getServer().getPluginManager().callEvent(rideEvent);
                        if (!rideEvent.isCancelled()) {
                            this.motY = rideEvent.getJumpHeight();
                            doJumpAnimation();
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException | IllegalStateException e) {
                    Logger.log(Logger.LogLevel.WARNING, "Failed to initiate Pet Jumping Motion for " + this.getPlayerOwner().getName() + "'s Pet.", e, true);
                }
            }
        }
    }

    // EntityInsentient
    @Override
    protected SoundEffect G() {
        return fromBukkit(this.getIdleSound());
    }

    // EntityInsentient
    @Override
    protected SoundEffect bS() {
        return fromBukkit(this.getDeathSound());
    }

    protected abstract Sound getIdleSound(); //idle sound

    protected abstract Sound getDeathSound(); //death sound

    @Override
    public abstract SizeCategory getSizeCategory();

    // Entity
    @Override
    public void m() { // Tick
        super.m();
        //this.C();
        onLive();

        if (this.petGoalSelector == null) {
            this.remove(false);
            return;
        }
        this.petGoalSelector.updateGoals();
    }

    // EntityLiving
    @Override
    protected void i() { // initialize entity
        super.i();
        initDatawatcher();
    }

    // Entity
    @Override
    protected void a(BlockPosition blockposition, Block block) {
        super.a(blockposition, block);
        this.a(blockposition.getX(), blockposition.getY(), blockposition.getZ(), block);
    }

    protected void a(int i, int j, int k, Block block) {
        super.a(new BlockPosition(i, j, k), block);
        makeStepSound(i, j, k, block);
    }

    protected void makeStepSound(int i, int j, int k, Block block) {
        this.makeStepSound();
    }

    protected void initDatawatcher() {
    }

    protected void makeStepSound() {
    }

    protected void doJumpAnimation() {
    }

    /*
     * Why are there multiple write and read methods?
     * NEVER ASK, OR YOU'LL GO CRAZY!!
     */

    @Override
    public void b(NBTTagCompound nbttagcompound) { // write to nbt
        // Do nothing with NBT
        // Pets should not be stored to world save files
    }

    @Override
    public boolean c(NBTTagCompound nbttagcompound) { // writeToNBT
        // Do nothing with NBT
        // Pets should not be stored to world save files
        return false;
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) { // readFromNBT
        // Do nothing with NBT
        // Pets should not be stored to world save files

        /*super.a(nbttagcompound);
        String owner = nbttagcompound.getString("EchoPet_OwnerName");
        PetType pt = this.getEntityPetType();
        if (pt != null) {
            this.pet = pt.getNewPetInstance(owner, this);
            if (this.pet != null) {
                EchoPet.getManager().loadRiderFromFile(this.getPet());
                this.initiateEntityPet();
            }
        }*/
    }

    @Override
    public boolean d(NBTTagCompound nbttagcompound) { // writeToNBT
        // Do nothing with NBT
        // Pets should not be stored to world save files
        return false;
    }

    @Override
    public void e(NBTTagCompound nbttagcompound) { // writeToNBT
        // Do nothing with NBT
        // Pets should not be stored to world save files
    }
}
