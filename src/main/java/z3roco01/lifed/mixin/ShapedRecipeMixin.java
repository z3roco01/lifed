package z3roco01.lifed.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import z3roco01.lifed.features.BannedItems;

@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin {
    // i dont know if assemble is the same as craft so idk :/
    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"), cancellable = true)
    private void craft(CraftingInput input, CallbackInfoReturnable<ItemStack> cir) {
        // if they are trying to craft a banned item, cancel it
        if(!BannedItems.canCraft(cir.getReturnValue().getItem()))
            cir.setReturnValue(ItemStack.EMPTY);
    }
}
