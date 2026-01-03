package z3roco01.lifed.features;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import z3roco01.lifed.Lifed;

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
        return !Lifed.config.bannedItems.contains(item) || !Lifed.config.uncraftableItems.contains(item);
    }

    /**
     * Called after the config is loaded, loads the ids as actual items
     */
    public static void init() {
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
