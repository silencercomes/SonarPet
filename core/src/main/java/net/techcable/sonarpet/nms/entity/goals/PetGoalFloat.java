package net.techcable.sonarpet.nms.entity.goals;

import com.dsh105.echopet.compat.api.ai.APetGoalFloat;
import com.dsh105.echopet.compat.api.ai.PetGoalType;

import net.techcable.sonarpet.nms.entity.EntityInsentientPet;

public class PetGoalFloat extends APetGoalFloat {

    private EntityInsentientPet pet;

    public PetGoalFloat(EntityInsentientPet pet) {
        this.pet = pet;
        pet.getEntity().setCanSwim(true);
    }

    @Override
    public PetGoalType getType() {
        return PetGoalType.FOUR;
    }

    @Override
    public String getDefaultKey() {
        return "Float";
    }

    @Override
    public boolean shouldStart() {
        return pet.getEntity().isInWater() || pet.getEntity().isInLava();
    }

    @Override
    public void tick() {
        if (this.pet.random().nextFloat() < 0.8F) {
            pet.getEntity().jump();
        }
    }
}