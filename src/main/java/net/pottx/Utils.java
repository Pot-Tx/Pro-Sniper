package net.pottx;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.pottx.access.PlayerEntityAccess;

public class Utils {
    public static double[] predictRelativeXZOnRangedHit(LivingEntity target, double initRelativeX, double initRelativeY, double initRelativeZ, double projectileVelocity){
        double targetVelocityX;
        double targetVelocityY;
        double targetVelocityZ;
        if (target instanceof ServerPlayerEntity) {
            targetVelocityX = ((PlayerEntityAccess) target).getRealVelocityX();
            targetVelocityY = ((PlayerEntityAccess) target).getRealVelocityY();
            targetVelocityZ = ((PlayerEntityAccess) target).getRealVelocityZ();
        } else {
            targetVelocityX = target.getVelocity().x;
            targetVelocityY = target.getVelocity().y;
            targetVelocityZ = target.getVelocity().z;
        }
        double a = targetVelocityX * targetVelocityX + targetVelocityY * targetVelocityY + targetVelocityZ * targetVelocityZ - projectileVelocity * projectileVelocity;
        double b = 2 * (
                initRelativeX * targetVelocityX
                        + (initRelativeY - (double) MathHelper.sqrt(initRelativeX * initRelativeX + initRelativeZ * initRelativeZ) * 0.20000000298023224) * targetVelocityY
                        + initRelativeZ * targetVelocityZ
        );
        double c = initRelativeX * initRelativeX
                + (initRelativeY - (double) MathHelper.sqrt(initRelativeX * initRelativeX + initRelativeZ * initRelativeZ) * 0.20000000298023224) * (initRelativeY - (double) MathHelper.sqrt(initRelativeX * initRelativeX + initRelativeZ * initRelativeZ) * 0.20000000298023224)
                + initRelativeZ * initRelativeZ;
        double time = ((double) MathHelper.sqrt(b * b - 4 * a * c) - b) / (2 * a);
        double relativeX = initRelativeX - 1.25 * targetVelocityX * time;
        double relativeZ = initRelativeZ - 1.25 * targetVelocityZ * time;
        return new double[] {relativeX, relativeZ};
    }
}
