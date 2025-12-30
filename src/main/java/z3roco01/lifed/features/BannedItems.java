package z3roco01.lifed.features;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import z3roco01.lifed.Lifed;

import java.util.ArrayList;

/**
 * Handles banned items, both in crafting and fully
 */
public class BannedItems {
    /**
     * Items banned in both crafting and possessing
     */
    public static final ArrayList<Item> FULLY_BANNED = new ArrayList<>();

    /**
     * Items which cannot be crafted but can be possessed
     */
    public static final ArrayList<Item> UNCRAFTABLE = new ArrayList<>();

    /**
     * Returns if the passed item is allowed to be crafted
     * @param item the item to test
     * @return true if it is not in either list, otherwise false
     */
    public static boolean canCraft(Item item) {
        return !FULLY_BANNED.contains(item) || !UNCRAFTABLE.contains(item);
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

    static {
        if(!Lifed.config.bookshelfAllowed)
            FULLY_BANNED.add(Items.BOOKSHELF);

        if(!Lifed.config.canCraftEnchanter)
            UNCRAFTABLE.add(Items.ENCHANTING_TABLE);
    }
}
