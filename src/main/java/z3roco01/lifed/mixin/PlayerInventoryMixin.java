package z3roco01.lifed.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.features.BannedItems;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable {
    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    private void addStack(int slot, ItemStack stack, CallbackInfo ci) {
        if(Lifed.config.bannedItems.contains(stack.getItem()))
            ci.cancel();
    }
}
