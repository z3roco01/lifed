package z3roco01.lifed.mixin;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import z3roco01.lifed.config.ConfigFiles;
import z3roco01.lifed.features.LifeManager;
import z3roco01.lifed.util.player.PlayerUtil;

@Mixin(Item.class)
public abstract class ItemMixin implements FeatureElement, ItemLike, FabricItem {
    @Inject(method = "use", at = @At("HEAD"))
    private void use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if(world.isClientSide()) return;

        if(!ConfigFiles.gameplay.totemsConvertable) return;

        // when item is right clicked, if its a totem and its allowed, turn it into a life
        ItemStack stack = user.getItemInHand(hand);
        DeathProtection deathProtection = stack.get(DataComponents.DEATH_PROTECTION);

        if(deathProtection != null) {
            stack.shrink(1);
            LifeManager.addLife((ServerPlayer) user);
            PlayerUtil.playTotemAnimation((ServerPlayer) user);
        }
    }
}
