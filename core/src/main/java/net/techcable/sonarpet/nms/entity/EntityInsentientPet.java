package net.techcable.sonarpet.nms.entity;

import lombok.*;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Random;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.dsh105.echopet.compat.api.ai.PetGoalSelector;
import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IEntityPet;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.event.PetAttackEvent;
import com.dsh105.echopet.compat.api.event.PetRideJumpEvent;
import com.dsh105.echopet.compat.api.event.PetRideMoveEvent;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import net.techcable.sonarpet.nms.INMS;
import com.dsh105.echopet.compat.api.util.Logger;
import com.dsh105.echopet.compat.api.util.MenuUtil;
import com.dsh105.echopet.compat.api.util.Perm;
import com.dsh105.echopet.compat.api.util.menu.MenuOption;
import com.dsh105.echopet.compat.api.util.menu.PetMenu;
import com.google.common.base.Preconditions;

import net.techcable.sonarpet.nms.DamageSource;
import net.techcable.sonarpet.nms.NMSEntity;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.NMSLivingEntity;
import net.techcable.sonarpet.nms.NMSPlayer;
import net.techcable.sonarpet.nms.entity.goals.PetGoalFloat;
import net.techcable.sonarpet.nms.entity.goals.PetGoalFollowOwner;
import net.techcable.sonarpet.nms.entity.goals.PetGoalLookAtPlayer;
import net.techcable.sonarpet.utils.reflection.Reflection;
import net.techcable.sonarpet.utils.reflection.SonarField;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class EntityInsentientPet implements IEntityPet {
    private final IPet pet;

    protected EntityInsentientPet(IPet pet) {
        this.pet = pet;
    }

    @Override
    public IPet getPet() {
        return pet;
    }

    public abstract NMSInsentientEntity getEntity();

    @Override
    public LivingEntity getBukkitEntity() {
        return getEntity().getBukkitEntity();
    }

    private double jumpHeight, rideSpeed;

    @OverridingMethodsMustInvokeSuper
    public void initiateEntityPet() {
        this.resetEntitySize();
        getBukkitEntity().setMaxHealth(getPet().getPetType().getMaxHealth());
        getBukkitEntity().setHealth((float) getPet().getPetType().getMaxHealth());
        jumpHeight = EchoPet.getOptions().getRideJumpHeight(this.getPet().getPetType());
        rideSpeed = EchoPet.getOptions().getRideSpeed(this.getPet().getPetType());
        this.setPathfinding();
    }

    public PetType getEntityPetType() {
        EntityPetType entityPetType = this.getClass().getAnnotation(EntityPetType.class);
        if (entityPetType != null) {
            return entityPetType.petType();
        }
        return null;
    }

    public boolean isPersistent() {
        return true;
    }

    public Player getPlayerOwner() {
        return getPet().getOwner();
    }

    public NMSPlayer getPlayerEntity() {
        return (NMSPlayer) INMS.getInstance().wrapEntity(getPlayerOwner());
    }

    public Location getLocation() {
        return getPet().getLocation();
    }

    private static final Class<?> NMS_ENTITY_CLASS = Reflection.getNmsClass("Entity");
    private static final SonarField<Random> RANDOM_SONAR_FIELD = SonarField.getField(NMS_ENTITY_CLASS, "random", Random.class);
    public Random random() {
        return RANDOM_SONAR_FIELD.getValue(Reflection.getHandle(getBukkitEntity()));
    }

    private PetGoalSelector petGoalSelector;
    @Override
    public PetGoalSelector getPetGoalSelector() {
        return petGoalSelector;
    }

    @Override
    public boolean isDead() {
        return getBukkitEntity().isDead();
    }

    private boolean shouldVanish;
    @Override
    public void setShouldVanish(boolean flag) {
        shouldVanish = flag;
    }

    @Override
    public void setTarget(LivingEntity livingEntity) {
        getEntity().setTarget(livingEntity);
    }

    @Override
    public LivingEntity getTarget() {
        return getEntity().getTarget();
    }

    public boolean  attack(Entity entity) {
        return this.attack(entity, (float) this.getPet().getPetType().getAttackDamage());
    }

    public boolean attack(Entity entity, float damage) {
        return this.attack(entity, INMS.getInstance().mobAttackDamageSource(getBukkitEntity()), damage);
    }

    public boolean attack(Entity entity, DamageSource damageSource, float damage) {
        PetAttackEvent attackEvent = new PetAttackEvent(this.getPet(), entity, damage);
        EchoPet.getPlugin().getServer().getPluginManager().callEvent(attackEvent);
        if (!attackEvent.isCancelled()) {
            if (entity instanceof Player) {
                if (!(EchoPet.getConfig().getBoolean("canAttackPlayers", false))) {
                    return false;
                }
            }
            return INMS.getInstance().wrapEntity(entity).damageEntity(damageSource, (float) attackEvent.getDamage());
        }
        return false;
    }


    public void setPathfinding() {
        try {
            petGoalSelector = new PetGoalSelector();
            getEntity().clearGoals();
            petGoalSelector.addGoal(new PetGoalFloat(this), 0);
            petGoalSelector.addGoal(new PetGoalFollowOwner(this, this.getSizeCategory().getStartWalk(getPet().getPetType()), this.getSizeCategory().getStopWalk(getPet().getPetType()), this.getSizeCategory().getTeleport(getPet().getPetType())), 1);
            petGoalSelector.addGoal(new PetGoalLookAtPlayer(this), 2);

        } catch (Exception e) {
            Logger.log(Logger.LogLevel.WARNING, "Could not add PetGoals to Pet AI.", e, true);
        }
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
    public void remove(boolean makeSound) {
        if (this.getEntity() != null) {
            getBukkitEntity().remove();
        }
        if (makeSound) {
            getEntity().playSound(getEntity().getDeathSound(), 1.0F, 1.0F);
        }
    }

    public void onLive() {
        if (this.getPet() == null) {
            this.remove(false);
            return;
        }

        if (this.getPlayerOwner() == null || !this.getPlayerOwner().isOnline()) {
            EchoPet.getManager().removePet(this.getPet(), true);
            return;
        }

        if (getPet().isOwnerRiding() && getEntity().getPassengers().isEmpty() && !getPet().isOwnerInMountingProcess()) {
            getPet().ownerRidePet(false);
        }

        if (getPlayerEntity().isInvisible() != getEntity().isInvisible() && !shouldVanish) {
            getEntity().setInvisible(!getEntity().isInvisible());
        }

        if (getPlayerEntity().isSneaking() != getEntity().isSneaking()) {
            getEntity().setSneaking(!getEntity().isSneaking());
        }

        if (getPlayerEntity().isSprinting() != getEntity().isSprinting()) {
            getEntity().setSprinting(!getEntity().isSprinting());
        }

        if (this.getPet().isHat()) {
            getEntity().setYaw(this.getPet().getPetType() == PetType.ENDERDRAGON ? this.getPlayerOwner().getLocation().getYaw() - 180 : this.getPlayerOwner().getLocation().getYaw());
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

            getBukkitEntity().setVelocity(new Vector(x, y, z).normalize().multiply(0.3F));
        }

        if (getEntity().getPassengers().isEmpty()) {
            petGoalSelector.updateGoals();
        }
    }

    /**
     * Return if the pet's owner is currently riding the pet
     *
     * @return if the owner is riding
     */
    public boolean isOwnerRiding() {
        for (NMSEntity passenger : getEntity().getPassengers()) {
            if (passenger.getBukkitEntity() == getPlayerOwner()) {
                return true;
            }
        }
        return false;
    }

    // EntityLiving

    public void onStep(int x, int y, int z) {
        makeStepSound(x, y, z, getBukkitEntity().getWorld().getBlockAt(x, y, z).getType());
    }

    /*
     * We need to override the move logic for special handling when the owner is riding
     */
    @SneakyThrows
    public void move(float sideMot, float forwMot, MethodHandle superMoveFunction) {
        Preconditions.checkNotNull(superMoveFunction, "Null superMoveFunction");
        getEntity().setStepHeight(1); // Give pets the ability to step up full blocks
        if (!isOwnerRiding()) {
            superMoveFunction.invoke(sideMot, forwMot); // moveEntity
            return;
        }
        getEntity().setYaw(getPlayerOwner().getLocation().getYaw());
        getEntity().setPitch(getPlayerOwner().getLocation().getPitch() * 0.5F);

        getEntity().correctYaw();

        sideMot = getPlayerEntity().getSidewaysMotion() * 0.5F;
        forwMot = getPlayerEntity().getForwardsMotion();

        if (forwMot <= 0.0F) {
            forwMot *= 0.25F; // quarter speed backwards
        }
        sideMot *= 0.75F; // 75% slower sideways

        PetRideMoveEvent moveEvent = new PetRideMoveEvent(this.getPet(), forwMot, sideMot);
        EchoPet.getPlugin().getServer().getPluginManager().callEvent(moveEvent);
        if (moveEvent.isCancelled()) {
            return;
        }

        getEntity().setMoveSpeed(rideSpeed); // set the movement speed
        superMoveFunction.invoke(moveEvent.getSidewardMotionSpeed(), moveEvent.getForwardMotionSpeed()); // superclass movement logic, with the speed from the movement event

        PetType pt = this.getPet().getPetType();
        if (EchoPet.getOptions().canFly(pt)) {
            try {
                if (getPlayerOwner().isFlying()) {
                    getPlayerOwner().setFlying(false);
                }
                if (getPlayerEntity().isJumping()) {
                    PetRideJumpEvent rideEvent = new PetRideJumpEvent(this.getPet(), jumpHeight);
                    EchoPet.getPlugin().getServer().getPluginManager().callEvent(rideEvent);
                    if (!rideEvent.isCancelled()) {
                        getEntity().setUpwardsMotion(0.5);
                    }
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                Logger.log(Logger.LogLevel.WARNING, "Failed to initiate Pet Flying Motion for " + this.getPlayerOwner().getName() + "'s Pet.", e, true);
            }
        } else if (getBukkitEntity().isOnGround()) {
            try {
                if (getPlayerEntity().isJumping()) {
                    PetRideJumpEvent rideEvent = new PetRideJumpEvent(this.getPet(), jumpHeight);
                    EchoPet.getPlugin().getServer().getPluginManager().callEvent(rideEvent);
                    if (!rideEvent.isCancelled()) {
                        getEntity().setUpwardsMotion(rideEvent.getJumpHeight());
                        doJumpAnimation();
                    }
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                Logger.log(Logger.LogLevel.WARNING, "Failed to initiate Pet Jumping Motion for " + this.getPlayerOwner().getName() + "'s Pet.", e, true);
            }
        }
    }

    public SizeCategory getSizeCategory() {
        return SizeCategory.REGULAR;
    }

    // Entity

    public void makeStepSound(int i, int j, int k, Material block) {
        this.makeStepSound();
    }

    public void makeStepSound() {
    }

    public void doJumpAnimation() {}

    @Override
    public void setInvisible(boolean flag) {
        getEntity().setInvisible(flag);
    }

    // Not neded anymore
    @Override
    public void resetEntitySize() {}

    @Override
    public void resizeBoundingBox(boolean flag) {}

    @Override
    public void setEntitySize(float width, float height) {}
}
