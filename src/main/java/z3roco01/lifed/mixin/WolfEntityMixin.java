package z3roco01.lifed.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.WolfCounter;

@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends TamableAnimal implements NeutralMob {
    protected WolfEntityMixin(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tryToTame", at = @At("HEAD"), cancellable = true)
    private void tryToTame(Player player, CallbackInfo ci) {
        if(player.level().isClientSide()) return;

        // if someone is trying to tame a wolf but has the max amount of wolfs, cancel it
        if(((WolfCounter) player).getWolfCount() == Lifed.config.wolfLimit)
            ci.cancel();
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        if(level().isClientSide()) return;

        // when a wolf has died, if its tamed, decrement the owners count
        if(getOwner() == null) return;
        ServerPlayer owner = (ServerPlayer) getOwner();
        ((WolfCounter) owner).decrementWolfCount();
    }

    @Redirect(method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/wolf/Wolf;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;isTame()Z"))
    private boolean makeChildTamedRedirect(Wolf instance) {
        // redirect from is tamed, when making a child, if the owner has the limit, make the puppy not owned
        WolfCounter owner = (WolfCounter) getOwner();
        if(owner.getWolfCount() == Lifed.config.wolfLimit)
            return false;
        else {
            owner.incrementWolfCount();
            return isTame();
        }
    }
}
