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

package com.dsh105.echopet.listeners;

import lombok.*;

import com.dsh105.echopet.EchoPetPlugin;
import com.dsh105.echopet.compat.api.entity.IEntityPet;
import com.dsh105.echopet.compat.api.event.PetDamageEvent;
import com.dsh105.echopet.compat.api.event.PetInteractEvent;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin;
import com.dsh105.echopet.compat.api.util.Lang;
import com.dsh105.echopet.compat.api.util.ReflectionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.vehicle.VehicleExitEvent;

@RequiredArgsConstructor
public class PetEntityListener implements Listener {
    private final IEchoPetPlugin plugin;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity e = event.getEntity();
        if (plugin.isPet(e)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawnUnBlock(CreatureSpawnEvent event) {
        Entity e = event.getEntity();
        if (plugin.isPet(e)) {
            if (event.isCancelled()) {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDismount(VehicleExitEvent event) {
        Entity e = event.getVehicle();
        if (plugin.isPet(e)) {
            IEntityPet entityPet = plugin.getPetEntity(e);
            if (entityPet.getPet().isOwnerRiding() && !entityPet.getPet().isOwnerInMountingProcess()) {
                Lang.sendTo(entityPet.getPet().getOwner(), Lang.RIDE_PET_OFF.toString());
                entityPet.getPet().ownerRidePet(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPetEnterPortal(EntityPortalEvent event) {
        Entity e = event.getEntity();
        if (plugin.isPet(e)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (plugin.isPet(e)) {
            IEntityPet entityPet = plugin.getPetEntity(e);
            PetDamageEvent damageEvent = new PetDamageEvent(entityPet.getPet(), event.getCause(), event.getDamage());
            EchoPet.getPlugin().getServer().getPluginManager().callEvent(damageEvent);
            event.setDamage(damageEvent.getDamage());
            event.setCancelled(damageEvent.isCancelled());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Entity e = event.getEntity();
        if (plugin.isPet(e)) {
            IEntityPet entityPet = plugin.getPetEntity(e);
            Entity damager = event.getDamager();
            if (damager instanceof Player) {
                PetInteractEvent iEvent = new PetInteractEvent(entityPet.getPet(), (Player) damager, PetInteractEvent.Action.LEFT_CLICK, true);
                EchoPet.getPlugin().getServer().getPluginManager().callEvent(iEvent);
                if (iEvent.isCancelled()) {
                    event.setCancelled(true);
                    event.setDamage(0);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        Entity e = event.getEntity();
        if (plugin.isPet(e)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockForm(EntityBlockFormEvent event) {
        Entity e = event.getEntity();
        if (plugin.isPet(e) && event.getNewState().getType().equals(Material.SNOW)) {
            event.setCancelled(true);
            event.getNewState().setType(Material.AIR);
        }
    }
}
