package z3roco01.lifed.mixin;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.features.LifeManager;
import z3roco01.lifed.util.player.PlayerUtil;

@Mixin(Item.class)
public abstract class ItemMixin implements ToggleableFeature, ItemConvertible, FabricItem {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if(world.isClient()) return;

        if(!Lifed.config.totemsConvertable) return;

        ItemStack stack = user.getStackInHand(hand);
        DeathProtectionComponent deathProtection = stack.get(DataComponentTypes.DEATH_PROTECTION);

        if(deathProtection != null) {
            stack.decrement(1);
            LifeManager.addLife((ServerPlayerEntity)user);
            PlayerUtil.playTotemAnimation((ServerPlayerEntity)user);
        }
    }
}
