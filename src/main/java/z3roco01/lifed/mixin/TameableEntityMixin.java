package z3roco01.lifed.mixin;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import z3roco01.lifed.util.WolfCounter;

@Mixin(TamableAnimal.class)
public abstract class TameableEntityMixin {
    @Inject(method = "tame", at = @At("HEAD"))
    private void tame(Player player, CallbackInfo ci) {
        // when a wolf is tamed, increment the tamers wolf count
        if((TamableAnimal)(Object)this instanceof Wolf)
            ((WolfCounter)(Object)player).incrementWolfCount();
    }
}
