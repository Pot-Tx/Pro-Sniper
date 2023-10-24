package net.pottx.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.pottx.Utils;
import net.pottx.access.PlayerEntityAccess;
import net.pottx.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin extends HostileEntity implements RangedAttackMob {

	protected AbstractSkeletonEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Unique
	private static final TrackedData<Boolean> IS_PRO = DataTracker.registerData(AbstractSkeletonEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(IS_PRO, false);
	}

	@Inject(
			method = "initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/EntityData;",
			at = @At("HEAD")
	)
	private void initializePro(CallbackInfoReturnable<EntityData> cir) {
		if (ConfigManager.configData.enable.enableSkeleton && this.random.nextFloat() < ConfigManager.configData.chance.skeletonChance[this.world.getDifficulty().getId()]) {
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

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt){
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("IsPro", this.getDataTracker().get(IS_PRO));
	}

	@ModifyArgs(
			method = "attack(Lnet/minecraft/entity/LivingEntity;F)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(DDDFF)V")
	)
	private void setPredictedPosition(Args args, LivingEntity target, float pullProgress){
		if(this.dataTracker.get(IS_PRO)) {
			args.set(0, Utils.predictRelativeXZOnRangedHit(target, args.get(0), args.get(1), args.get(2), 1.6)[0]);
			args.set(2, Utils.predictRelativeXZOnRangedHit(target, args.get(0), args.get(1), args.get(2), 1.6)[1]);
		}
	}
}