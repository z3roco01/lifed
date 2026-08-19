package z3roco01.lifed.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import z3roco01.lifed.config.ConfigFiles;

@Mixin(AbstractContainerMenu.class)
public abstract class PlayerInventoryMixin {
    // idk if thisll work since had to move from playerinventory to generic and grrrr frick you mojang
    @Inject(method = "setRemoteSlot", at = @At("HEAD"), cancellable = true)
    private void addStack(int slot, ItemStack stack, CallbackInfo ci) {
        // only do on player inventory, but actually maybe change this idk
        if(!(((AbstractContainerMenu)(Object)this) instanceof InventoryMenu))
            return;
        // if they are trying to add a banned item to their inventory, dont let them
        if(ConfigFiles.gameplay.bannedItems.contains(stack.getItem()))
            ci.cancel();
    }
}
