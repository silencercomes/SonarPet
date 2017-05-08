package net.techcable.sonarpet.nms;

import javax.annotation.Nullable;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public interface NMSInsentientEntity extends NMSLivingEntity {
    @Override
    LivingEntity getBukkitEntity();

    void lookAt(Entity other, float perTick, float verticalFaceSpeed);

    void clearGoals();

    @Nullable
    NMSSound getDeathSound();

    Navigation getNavigation();

    void setFollowRange(double followRange);

    LivingEntity getTarget();

    void setTarget(LivingEntity target);

    void setCanSwim(boolean b);

    void jump();

    float getVerticalFaceSpeed();

    Object getHandle();
}
