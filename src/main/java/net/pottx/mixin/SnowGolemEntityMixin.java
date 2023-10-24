package net.pottx.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.pottx.Utils;
import net.pottx.config.ConfigManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SnowGolemEntity.class)
public abstract class SnowGolemEntityMixin extends GolemEntity implements Shearable, RangedAttackMob {

    protected SnowGolemEntityMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final TrackedData<Boolean> IS_PRO = DataTracker.registerData(SnowGolemEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(
            method = "initDataTracker()V",
            at = @At("TAIL")
    )
    public void initProTracking(CallbackInfo ci) {
        this.dataTracker.startTracking(IS_PRO, false);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        if (ConfigManager.configData.enable.enableSnowGolem && this.random.nextFloat() < ConfigManager.configData.chance.snowGolemChance[this.world.getDifficulty().getId()]) {
            this.dataTracker.set(IS_PRO, true);
        } else {
            this.dataTracker.set(IS_PRO, false);
        }
        return entityData;
    }

    @Inject(
            method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V",
            at = @At("TAIL")
    )
    private void addProDataRead(NbtCompound nbt, CallbackInfo ci){
        this.getDataTracker().set(IS_PRO, nbt.getBoolean("IsPro"));
    }

    @Inject(
            method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V",
            at = @At("TAIL")
    )
    private void addProDataWrite(NbtCompound nbt, CallbackInfo ci){
        nbt.putBoolean("IsPro", this.getDataTracker().get(IS_PRO));
    }

    @ModifyArgs(
            method = "attack(Lnet/minecraft/entity/LivingEntity;F)V",
            at = @At(value = "INVOKE", target = "net/minecraft/entity/projectile/thrown/SnowballEntity.setVelocity (DDDFF)V")
    )
    private void setPredictedPosition(Args args, LivingEntity target, float pullProgress){
        if(this.dataTracker.get(IS_PRO)) {
            args.set(0, Utils.predictRelativeXZOnRangedHit(target, args.get(0), args.get(1), args.get(2), 1.6)[0]);
            args.set(2, Utils.predictRelativeXZOnRangedHit(target, args.get(0), args.get(1), args.get(2), 1.6)[1]);
        }
    }
}
