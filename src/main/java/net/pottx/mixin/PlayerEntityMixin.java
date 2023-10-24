package net.pottx.mixin;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.pottx.access.PlayerEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccess {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private double realPrevX = 0;

    @Unique
    private double realVelocityX = 0;

    @Unique
    private double realPrevY = 0;

    @Unique
    private double realVelocityY = 0;

    @Unique
    private double realPrevZ = 0;

    @Unique
    private double realVelocityZ = 0;

    @Inject(
            method = "tick()V",
            at = @At("TAIL")
    )
    private void trackRealPlayerVelocity(CallbackInfo ci) {
        this.realVelocityX = this.getX() - this.realPrevX;
        this.realPrevX = this.getX();

        this.realVelocityY = this.getY() - this.realPrevY;
        this.realPrevY = this.getY();

        this.realVelocityZ = this.getZ() - this.realPrevZ;
        this.realPrevZ = this.getZ();
    }

    @Override
    public double getRealVelocityX() {
        return this.realVelocityX;
    }

    @Override
    public double getRealVelocityY() {
        return this.realVelocityY;
    }

    @Override
    public double getRealVelocityZ() {
        return this.realVelocityZ;
    }
}
