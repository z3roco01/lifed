package z3roco01.lifed.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.WolfCounter;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntity implements Angerable {
    protected WolfEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tryTame", at = @At("HEAD"), cancellable = true)
    private void tryTame(PlayerEntity player, CallbackInfo ci) {
        if(player.getEntityWorld().isClient()) return;

        if(((WolfCounter)(Object)player).getWolfCount() == Lifed.config.wolfLimit)
            ci.cancel();
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        if(getEntityWorld().isClient()) return;

        ServerPlayerEntity owner = (ServerPlayerEntity)getOwner();
        ((WolfCounter)(Object)owner).decrementWolfCount();
    }

    @Redirect(method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/WolfEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/WolfEntity;isTamed()Z"))
    private boolean makeChildTamedRedirect(WolfEntity instance) {
        WolfCounter owner = (WolfCounter)(Object)getOwner();
        if(owner.getWolfCount() == Lifed.config.wolfLimit)
            return false;
        else {
            owner.incrementWolfCount();
            return isTamed();
        }
    }
}
