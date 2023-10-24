package net.pottx.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.math.MathHelper;
import net.pottx.Utils;
import net.pottx.access.GhastEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(targets = "net.minecraft.entity.mob.GhastEntity$ShootFireballGoal")
public abstract class ShootFireballGoalMixin extends Goal {
    public ShootFireballGoalMixin() {}

    @ModifyArgs(
            method = "tick()V",
            at = @At(value = "INVOKE", target = "net/minecraft/entity/projectile/FireballEntity.<init> (Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;DDD)V")
    )
    private void setPredictedPosition(Args args) {
        if (((GhastEntityAccess)args.get(1)).getIsPro()) {
            double distance = MathHelper.sqrt((double) args.get(2) * (double) args.get(2)
                    + (double) args.get(3) * (double) args.get(3) + (double) args.get(4) * (double) args.get(4));
            double averageVelocity = MathHelper.sqrt((distance * 0.095) / 2);
            LivingEntity target = ((GhastEntity) args.get(1)).getTarget();
            if (target != null) {
                args.set(2, Utils.predictRelativeXZOnRangedHit(target, args.get(2), args.get(3), args.get(4), averageVelocity)[0]);
                args.set(4, Utils.predictRelativeXZOnRangedHit(target, args.get(2), args.get(3), args.get(4), averageVelocity)[1]);
            }
        }
    }
}
