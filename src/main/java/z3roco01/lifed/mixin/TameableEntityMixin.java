package z3roco01.lifed.mixin;

import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.WolfCounter;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin {
    @Inject(method = "setTamedBy", at = @At("HEAD"))
    private void setTamedBy(PlayerEntity player, CallbackInfo ci) {
        if((TameableEntity)(Object)this instanceof WolfEntity)
            ((WolfCounter)(Object)player).incrementWolfCount();
    }
}
