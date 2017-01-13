package net.techcable.sonarpet.nms.entity.goals;

import com.dsh105.echopet.compat.api.ai.APetGoalFollowOwner;
import com.dsh105.echopet.compat.api.ai.PetGoalType;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.event.PetMoveEvent;
import com.dsh105.echopet.compat.api.plugin.EchoPet;

import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.Navigation;
import net.techcable.sonarpet.nms.PathEntity;

import org.bukkit.entity.Player;

public class PetGoalFollowOwner extends APetGoalFollowOwner {

    private EntityInsentientPet pet;
    private Navigation nav;
    private int timer = 0;
    private double startDistance;
    private double stopDistance;
    private double teleportDistance;
    //private EntityPlayer owner;

    public PetGoalFollowOwner(EntityInsentientPet pet, double startDistance, double stopDistance, double teleportDistance) {
        this.pet = pet;
        this.nav = pet.getEntity().getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.teleportDistance = teleportDistance;
        //this.owner = ((CraftPlayer) pet.getPlayerOwner()).getHandle();
    }

    @Override
    public PetGoalType getType() {
        return PetGoalType.THREE;
    }

    @Override
    public String getDefaultKey() {
        return "FollowOwner";
    }

    @Override
    public boolean shouldStart() {
        if (this.pet.getBukkitEntity().isDead()) {
            return false;
        } else if (this.pet.getPlayerOwner() == null) {
            return false;
        } else if (this.pet.getPet().isOwnerRiding() || this.pet.getPet().isHat()) {
            return false;
        } else if (this.pet.getEntity().distanceTo(pet.getPlayerOwner()) < this.startDistance) { // pet.distanceSquared(this.pet.getPlayerOwner()) < startDistance
            return false;
        } else if (this.pet.getEntityPetType() == PetType.ENDERDRAGON) {
            return false;
        } else {
            return !(this.pet.getEntity().getTarget() != null && !this.pet.getEntity().getTarget().isDead());
        }

    }

    @Override
    public boolean shouldContinue() {
        if (this.nav.canEnterDoors()) { // if is able enter doors
            return false;
        } else if (this.pet.getPlayerOwner() == null) {
            return false;
        } else if (this.pet.getPet().isOwnerRiding() || this.pet.getPet().isHat()) {
            return false;
        } else if (this.pet.getEntity().distanceTo(pet.getPlayerOwner()) <= this.stopDistance) { // pet.distanceSquared(this.pet.getPlayerOwner()) < stopDistance
            return false;
        }
        //PetGoalMeleeAttack attackGoal = (PetGoalMeleeAttack) this.pet.petGoalSelector.getGoal("Attack");
        //return !(attackGoal != null && attackGoal.isActive);
        return true;
    }

    @Override
    public void start() {
        this.timer = 0;

        //Set pathfinding radius
        pet.getEntity().setFollowRange(this.teleportDistance);
    }

    @Override
    public void finish() {
        this.nav.finish();
    }

    @Override
    public void tick() {
        Player owner = pet.getPlayerOwner();
        /*
         * Make the pet look at the owner.
         * Changing up to 10 yaw in the pet's facing (max) per tick.
         * The speed to change the pitch at is requested from the entity.
         * Also done in PetGoalLookAtPlayer.tick()
         */
        this.pet.getEntity().lookAt(owner, 10.0F, (float) this.pet.getEntity().getVerticalFaceSpeed());
        if (--this.timer <= 0) {
            this.timer = 10;
            if (this.pet.getPlayerOwner().isFlying()) {
                //Don't move pet when owner flying
                return;
            }

            double speed = 0.6F;
            if (this.pet.getEntity().distanceTo(owner) > (this.teleportDistance) && pet.getPlayerEntity().isOnGround()) {
                this.pet.getPet().teleportToOwner();
                return;
            }

            PetMoveEvent moveEvent = new PetMoveEvent(this.pet.getPet(), this.pet.getLocation(), this.pet.getPlayerOwner().getLocation());
            EchoPet.getPlugin().getServer().getPluginManager().callEvent(moveEvent);
            if (moveEvent.isCancelled()) {
                return;
            }

            if (pet.getEntity().getTarget() == null) {
                PathEntity path;
                if (pet.getEntityPetType() == PetType.GHAST) {
                    path = pet.getEntity().getNavigation().getPathToLocation(pet.getPlayerOwner().getLocation().getBlockX(), pet.getPlayerOwner().getLocation().getBlockY() + 5, pet.getPlayerOwner().getLocation().getBlockZ()); // getPathToLocation
                } else {
                    path = pet.getEntity().getNavigation().getPathTo(owner); // getPathTo
                }

                //Smooth path finding to entity instead of location
                pet.getEntity().getNavigation().navigateTo(path, speed); // Set a new path for the entity to follow
            }
        }
    }
}