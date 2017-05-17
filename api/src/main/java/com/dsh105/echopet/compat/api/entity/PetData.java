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

package com.dsh105.echopet.compat.api.entity;

import java.util.Arrays;

import com.google.common.collect.ImmutableSet;

import net.techcable.sonarpet.utils.PrettyEnum;
import net.techcable.sonarpet.utils.compat.GuavaCompatibility;

/**
 * A data attribute a pet can have, stored as an on/off flag.
 *
 * A pet's data is stored in a bit vector, so <b>the enum must never be reordered</b>.
 * New types can only be added at the end, not at the beginning.
 */
public enum PetData implements PrettyEnum {

    ANGRY("angry", Type.BOOLEAN),
    BABY("baby", Type.BOOLEAN),
    BLACK("black", Type.COLOUR, Type.CAT, Type.HORSE_VARIANT, Type.RABBIT_TYPE),
    BLACK_AND_WHITE("blackandwhite", Type.RABBIT_TYPE),
    BLACKSMITH("blacksmith", Type.PROF),
    BLACKSPOT("blackSpot", Type.HORSE_MARKING),
    BLUE("blue", Type.COLOUR, Type.PARROT_COLOR),
    BROWN("brown", Type.COLOUR, Type.HORSE_VARIANT, Type.RABBIT_TYPE),
    BUTCHER("butcher", Type.PROF),
    CHESTED("chested", Type.BOOLEAN),
    CHESTNUT("chestnut", Type.HORSE_VARIANT),
    CREAMY("creamy", Type.HORSE_VARIANT),
    CYAN("cyan", Type.COLOUR, Type.PARROT_COLOR),
    DARKBROWN("darkbrown", Type.HORSE_VARIANT),
    DIAMOND("diamond", Type.HORSE_ARMOUR),
    DONKEY("donkey", Type.HORSE_TYPE),
    ELDER("elder", Type.BOOLEAN),
    FARMER("farmer", Type.PROF),
    FIRE("fire", Type.BOOLEAN),
    GRAY("gray", Type.COLOUR, Type.HORSE_VARIANT, Type.PARROT_COLOR),
    GREEN("green", Type.COLOUR),
    GOLD("gold", Type.HORSE_ARMOUR),
    IRON("iron", Type.HORSE_ARMOUR),
    THE_KILLER_BUNNY("killerbunny", Type.RABBIT_TYPE),
    LARGE("large", Type.SIZE),
    LIBRARIAN("librarian", Type.PROF),
    LIGHTBLUE("lightBlue", Type.COLOUR),
    LIME("lime", Type.COLOUR, Type.PARROT_COLOR),
    MAGENTA("magenta", Type.COLOUR),
    MEDIUM("medium", Type.SIZE),
    MULE("mule", Type.HORSE_TYPE),
    NOARMOUR("noarmour", Type.HORSE_ARMOUR),
    NONE("noMarking", Type.HORSE_MARKING),
    NORMAL("normal", Type.HORSE_TYPE, Type.ZOMBIE_TYPE, Type.SKELETON_TYPE),
    ORANGE("orange", Type.COLOUR),
    PINK("pink", Type.COLOUR),
    POWER("powered", Type.BOOLEAN),
    PRIEST("priest", Type.PROF),
    PURPLE("purple", Type.COLOUR),
    RED("red", Type.CAT, Type.COLOUR, Type.PARROT_COLOR),
    SADDLE("saddle", Type.BOOLEAN),
    SALT_AND_PEPPER("saltandpepper", Type.RABBIT_TYPE),
    SCREAMING("screaming", Type.BOOLEAN),
    SHEARED("sheared", Type.BOOLEAN),
    SHIELD("shield", Type.BOOLEAN),
    SIAMESE("siamese", Type.CAT),
    SILVER("silver", Type.COLOUR),
    SKELETON("skeleton", Type.HORSE_TYPE),
    SMALL("small", Type.SIZE),
    SOCKS("whiteSocks", Type.HORSE_MARKING),
    TAMED("tamed", Type.BOOLEAN),
    VILLAGER("villager", Type.ZOMBIE_TYPE),
    WHITEPATCH("whitePatch", Type.HORSE_MARKING),
    WHITESPOT("whiteSpot", Type.HORSE_MARKING),
    WHITE("white", Type.COLOUR, Type.HORSE_VARIANT, Type.RABBIT_TYPE),
    WILD("wild", Type.CAT),
    WITHER("wither", Type.SKELETON_TYPE),
    YELLOW("yellow", Type.COLOUR),
    ZOMBIE("zombie", Type.HORSE_TYPE),
    HUSK("husk", Type.ZOMBIE_TYPE),
    PIGMAN("pigman", Type.ZOMBIE_TYPE),
    STRAY("stray", Type.SKELETON_TYPE);

    private final String configOptionString;
    private final ImmutableSet<Type> types;

    PetData(String configOptionString, Type... types) {
        this.configOptionString = configOptionString;
        this.types = ImmutableSet.copyOf(types);
    }

    public String getConfigOptionString() {
        return this.configOptionString;
    }

    public ImmutableSet<Type> getTypes() {
        return this.types;
    }

    public boolean isType(Type t) {
        return this.types.contains(t);
    }

    public enum Type {
        BOOLEAN,
        COLOUR,
        CAT,
        SIZE,
        PROF,
        HORSE_TYPE,
        HORSE_VARIANT,
        HORSE_MARKING,
        HORSE_ARMOUR,
        RABBIT_TYPE,
        ZOMBIE_TYPE,
        SKELETON_TYPE,
        PARROT_COLOR;

        private ImmutableSet<PetData> values;
        public ImmutableSet<PetData> getValues() {
            ImmutableSet<PetData> values = this.values;
            if (values == null) {
                this.values = values = Arrays.stream(PetData.values())
                        .filter((data) -> data.isType(this))
                        .collect(GuavaCompatibility.INSTANCE.immutableSetCollector());
            }
            return values;
        }
        public PetData[] getValueArray() {
            ImmutableSet<PetData> values = this.getValues();
            return values.toArray(new PetData[values.size()]);
        }
    }
}
