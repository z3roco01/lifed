package z3roco01.lifed.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.features.SoulmateManager;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, WaypointTransmitter {
    @Shadow
    @Final
    private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void hurtServer(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if(!(this instanceof SoulmateManager.SoulmateHaver))
            return;

        // dont do damage since it should be coming from soulmate
        if(((SoulmateManager.SoulmateHaver)this).getIncomingDamage() == damage) {
            ((SoulmateManager.SoulmateHaver)this).clearIncoming();
            return;
        }

        ServerPlayer soulmate = ((SoulmateManager.SoulmateHaver)this).getSoulmatePlayer();
        if(soulmate == null)
            return;

        ((SoulmateManager.SoulmateHaver)soulmate).incomingDamage(damage);
        soulmate.hurtServer(level, new DamageSource(source.typeHolder(), Vec3.ZERO), damage);
    }

    @Inject(method = "heal", at = @At("HEAD"))
    private void heal(float heal, CallbackInfo ci) {
        // do nothing if theres no soulmate
        if(!(this instanceof SoulmateManager.SoulmateHaver soulmateHaver))
            return;

        // also we need a soulmate to do stuff
        if(soulmateHaver.getSoulmatePlayer() == null)
            return;

        ServerPlayer soulmate = soulmateHaver.getSoulmatePlayer();

        float health = soulmate.getHealth();
        if (health > 0.0F) {
            soulmate.setHealth(health + heal);
        }
    }

    @Inject(method = "setAbsorptionAmount", at = @At("HEAD"))
    private void setAbsorptionAmount(float absorptionAmount, CallbackInfo ci) {
        // do nothing if theres no soulmate
        if(!(this instanceof SoulmateManager.SoulmateHaver soulmateHaver))
            return;

        // also we need a soulmate to do stuff
        if(soulmateHaver.getSoulmatePlayer() == null)
            return;

        ServerPlayer soulmate = soulmateHaver.getSoulmatePlayer();
        soulmate.internalSetAbsorptionAmount(absorptionAmount);
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void die(DamageSource source, CallbackInfo ci) {

    }
}
