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

import lombok.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.dsh105.echopet.compat.api.entity.IEntityPet;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin;
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
        return Helper.noArgsConstructor != null || Helper.pluginArgConstructor != null;
    }

    @SneakyThrows // Won't throw an exception
    public static INMS createInstance(IEchoPetPlugin plugin) {
        Preconditions.checkNotNull(plugin, "Null plugin");
        synchronized (Helper.instances) {
            Preconditions.checkState(!Helper.instances.containsKey(plugin), "NMS already created for plugin");
            final INMS nms;
            if (Helper.pluginArgConstructor != null) {
                nms = (INMS) Helper.pluginArgConstructor.invoke();
            } else if (Helper.noArgsConstructor != null) {
                nms = (INMS) Helper.noArgsConstructor.invoke(plugin);
            } else {
                throw new UnsupportedOperationException("Unsupported version");
            }
            Helper.instances.put(plugin, nms);
            return nms;
        }
    }

}

/**
 * A helper for NMS getInstance()
 */
@Deprecated
class Helper {
    public static final Map<IEchoPetPlugin, INMS> instances = Collections.synchronizedMap(new HashMap<>());
    public static MethodHandle noArgsConstructor, pluginArgConstructor;

    static  {
        synchronized (instances) {
            Class<?> implClass = Reflection.getClass("com.dsh105.echopet.compat.nms." + NMS_VERSION + ".NMSImpl");
            if (implClass != null) {
                try {
                    try {
                        noArgsConstructor = MethodHandles.publicLookup().findConstructor(implClass, MethodType.methodType(void.class));
                    } catch (NoSuchMethodException e) {
                        try {
                            pluginArgConstructor = MethodHandles.publicLookup().findConstructor(implClass, MethodType.methodType(void.class, IEchoPetPlugin.class));
                        } catch (NoSuchMethodException e2) {
                            throw new RuntimeException("Can't find constructor");
                        }
                    }
                } catch (Throwable t) {
                    throw new AssertionError("Unable to invoke constructor", t);
                }
            }
        }
    }
}
