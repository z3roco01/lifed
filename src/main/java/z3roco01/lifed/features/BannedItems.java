package z3roco01.lifed.features;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import z3roco01.lifed.config.ConfigFiles;

/**
 * Handles banned items, both in crafting and fully
 */
public class BannedItems {

    /**
     * Returns if the passed item is allowed to be crafted
     * @param item the item to test
     * @return true if it is not in either list, otherwise false
     */
    public static boolean canCraft(Item item) {
        return !ConfigFiles.gameplay.bannedItems.contains(item) && !ConfigFiles.gameplay.uncraftableItems.contains(item);
    }

    /**
     * Check if an item can be crafted, if not, cancel the method
     * @param cir the callback info from a mixin inject
     */
    public static void cancelCrafting(CallbackInfoReturnable<ItemStack> cir) {
        Item attemptCraft = cir.getReturnValue().getItem();

        if(BannedItems.canCraft(attemptCraft)) {
            cir.setReturnValue(new ItemStack(Items.AIR));
            cir.cancel();
        }
    }
}
