package net.pottx.mixin;

import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.pottx.Utils;
import net.pottx.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PillagerEntity.class)
public abstract class PillagerEntityMixin extends IllagerEntity implements CrossbowUser {
    protected PillagerEntityMixin(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final TrackedData<Boolean> IS_PRO = DataTracker.registerData(PillagerEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(
            method = "initDataTracker()V",
            at = @At("TAIL")
    )
    public void initProTracking(CallbackInfo ci) {
        this.dataTracker.startTracking(IS_PRO, false);
    }

    @Inject(
            method = "initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/EntityData;",
            at = @At("HEAD")
    )
    private void initializePro(CallbackInfoReturnable<EntityData> cir) {
        if (ConfigManager.configData.enable.enablePillager && this.random.nextFloat() < ConfigManager.configData.chance.pillagerChance[this.world.getDifficulty().getId()]) {
            this.dataTracker.set(IS_PRO, true);
        } else {
            this.dataTracker.set(IS_PRO, false);
        }
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
    public void shoot(LivingEntity entity, LivingEntity target, ProjectileEntity projectile, float multishotSpray, float speed) {
        double d = target.getX() - entity.getX();
        double e = target.getZ() - entity.getZ();
        double f = (double) MathHelper.sqrt(d * d + e * e);
        double g = target.getBodyY(0.3333333333333333) - projectile.getY() + f * 0.20000000298023224;
        Vec3f vec3f;
        if (this.dataTracker.get(IS_PRO)){
            vec3f = this.getProjectileLaunchVelocity(entity, new Vec3d(Utils.predictRelativeXZOnRangedHit(target, d, g, e, 1.6)[0], g, Utils.predictRelativeXZOnRangedHit(target, d, g, e, 1.275)[1]), multishotSpray);
        } else {
            vec3f = this.getProjectileLaunchVelocity(entity, new Vec3d(d, g, e), multishotSpray);
        }
        projectile.setVelocity((double)vec3f.getX(), (double)vec3f.getY(), (double)vec3f.getZ(), speed, (float)(14 - entity.world.getDifficulty().getId() * 4));
        entity.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
