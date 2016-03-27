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

package com.dsh105.echopet.compat.api.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.dsh105.echopet.compat.api.entity.IEntityPet;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.google.common.base.Preconditions;

import net.techcable.sonarpet.utils.Versioning;
import net.techcable.sonarpet.utils.reflection.Reflection;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static net.techcable.sonarpet.utils.Versioning.NMS_VERSION;

@SuppressWarnings("deprecation")
public interface INMS {

    public IEntityPet spawn(IPet pet, Player owner);

    public default void mount(Entity rider, Entity vehicle) {
        Preconditions.checkNotNull(rider, "Null rider");
        if (vehicle != null) {
            vehicle.setPassenger(rider);
        } else {
            rider.leaveVehicle();
        }
    }

    public static boolean isSupported() {
        return Helper.instance != null;
    }

    public static INMS getInstance() {
        if (isSupported()) {
            return Helper.instance;
        } else {
            throw new UnsupportedOperationException("Unsupported version");
        }
    }

}

/**
 * A helper for NMS getInstance()
 */
@Deprecated
class Helper {

    public static final INMS instance;

    static {
        MethodHandle constructor;
        Class<?> implClass = Reflection.getClass("com.dsh105.echopet.compat.nms." + NMS_VERSION + ".NMSImpl");
        if (implClass == null) {
            instance = null;
        } else {
            try {
                constructor = MethodHandles.publicLookup().findConstructor(implClass, MethodType.methodType(void.class));
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new AssertionError("Unable to invoke constructor", e);
            }
            try {
                instance = constructor == null ? null : (INMS) constructor.invoke();
            } catch (Throwable t) {
                throw new AssertionError("NMS constructor threw exception", t);
            }
        }
    }
}
