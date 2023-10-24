package net.pottx.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.pottx.access.GhastEntityAccess;
import net.pottx.config.ConfigManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GhastEntity.class)
public abstract class GhastEntityMixin extends FlyingEntity implements Monster, GhastEntityAccess {

    protected GhastEntityMixin(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final TrackedData<Boolean> IS_PRO = DataTracker.registerData(GhastEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

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
        if (ConfigManager.configData.enable.enableGhast && this.random.nextFloat() < ConfigManager.configData.chance.ghastChance[this.world.getDifficulty().getId()]) {
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

    @Override
    public boolean getIsPro() {
        return this.dataTracker.get(IS_PRO);
    }
}
