package z3roco01.lifed.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import z3roco01.lifed.features.BannedItems;

@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin<T extends RecipeInput> {
    @Inject(method = "craft*", cancellable = true, at = @At("RETURN"))
    private void returnCraft(T par1, RegistryWrapper.WrapperLookup par2, CallbackInfoReturnable<ItemStack> cir) {
        BannedItems.cancelCrafting(cir);
    }
}
