package z3roco01.lifed.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.config.ConfigFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Shadow
    public static DataComponentType<ItemEnchantments> getComponentType(ItemStack itemStack) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    /**
     * List of pvp enchantments, for limiting their level
     */
    @Unique
    private static final ResourceKey<Enchantment>[] PVP_ENCHANTS = new ResourceKey[]{
            Enchantments.BLAST_PROTECTION,
            Enchantments.BREACH,
            Enchantments.DENSITY,
            Enchantments.FIRE_ASPECT,
            Enchantments.FIRE_PROTECTION,
            Enchantments.FLAME,
            Enchantments.IMPALING,
            Enchantments.INFINITY,
            Enchantments.KNOCKBACK,
            Enchantments.LOYALTY,
            Enchantments.LUNGE,
            Enchantments.MULTISHOT,
            Enchantments.PIERCING,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.PROTECTION,
            Enchantments.POWER,
            Enchantments.PUNCH,
            Enchantments.QUICK_CHARGE,
            Enchantments.RIPTIDE,
            Enchantments.SHARPNESS,
            Enchantments.SWEEPING_EDGE,
            Enchantments.THORNS,
            Enchantments.WIND_BURST,
    };

    /**
     * All other enchantments
     */
    @Unique
    private static final ResourceKey<Enchantment>[] NON_PVP_ENCHANTS = new ResourceKey[]{
            Enchantments.AQUA_AFFINITY,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.BINDING_CURSE,
            Enchantments.DEPTH_STRIDER,
            Enchantments.EFFICIENCY,
            Enchantments.FEATHER_FALLING,
            Enchantments.FORTUNE,
            Enchantments.FROST_WALKER,
            Enchantments.LOOTING,
            Enchantments.LUCK_OF_THE_SEA,
            Enchantments.LURE,
            Enchantments.MENDING,
            Enchantments.RESPIRATION,
            Enchantments.SILK_TOUCH,
            Enchantments.SMITE,
            Enchantments.SOUL_SPEED,
            Enchantments.SWIFT_SNEAK,
            Enchantments.UNBREAKING,
            Enchantments.VANISHING_CURSE,
    };

    /**
     * Returns if the entry's key is in the pvp oldEnchants list
     * @param entry the entry to check
     * @return true if it is in the array, also always returns false when pvp oldEnchants are allowed
     */
    @Unique
    private static boolean isPvpEnchant(Holder<Enchantment> entry) {
        if(ConfigFiles.gameplay.highLevelPvpEnchAllowed) return false;

        for(ResourceKey<Enchantment> key : PVP_ENCHANTS)
            if(entry.is(key)) return true;

        return false;
    }

    /**
     * Returns if the entry's key is in the non pvp oldEnchants list
     * @param entry the entry to check
     * @return true if it is in the array, also returns false always when non pvp enchantsa re allowed
     */
    @Unique
    private static boolean isNonPvpEnchant(Holder<Enchantment> entry) {
        if(ConfigFiles.gameplay.highLevelOtherEnchAllowed) return false;

        for(ResourceKey<Enchantment> key : NON_PVP_ENCHANTS)
            if(entry.is(key)) return true;

        return false;
    }

    /**
     * Checks if an enchantment entry is considered high level ( level > 1 )
     * @param entry entry to check
     * @return true if the leve is higher than one
     */
    @Unique
    private static boolean isEnchantHighLevel(EnchantmentInstance entry) {
        return entry.level() > 1;
    }

    // idk if this is the same or anything...
    @Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"), cancellable = true)
    private static void getPossibleEntries(int level, ItemStack stack, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        // if nothing is disallowed, then get out of here
        if(ConfigFiles.gameplay.highLevelOtherEnchAllowed && ConfigFiles.gameplay.highLevelPvpEnchAllowed) return;

        // array list that will be full of the appropriate oldEnchants
        List<EnchantmentInstance> newList = new ArrayList<>();

        for(EnchantmentInstance entry : cir.getReturnValue()) {
            // dont need to even consider this entry if its not high level
            Holder<Enchantment> enchant = entry.enchantment();

            if(ConfigFiles.gameplay.mendingBanned && enchant.is(Enchantments.MENDING))
                continue;

            // if it is disallowed, add it as level 1 to the list
            if(isEnchantHighLevel(entry) && (isPvpEnchant(enchant) || isNonPvpEnchant(enchant)))
                newList.add(new EnchantmentInstance(enchant, 1));
            else
                newList.add(entry);
        }
        cir.setReturnValue(newList);
    }

    @Inject(method = "setEnchantments", at = @At("HEAD"), cancellable = true)
    private static void set(ItemStack stack, ItemEnchantments enchantments, CallbackInfo ci) {
        // creates a new enchant component, and fill it with capped enchants
        ItemEnchantments.Mutable newEnchants = new ItemEnchantments.Mutable(enchantments);

        for(Holder<Enchantment> enchant : enchantments.keySet()) {

            int level = enchantments.getLevel(enchant);

            // set this levels enchant to become 1
            if(level > 1 && (isPvpEnchant(enchant) || isNonPvpEnchant(enchant))) {
                newEnchants.set(enchant, 1);
            }

            if(ConfigFiles.gameplay.mendingBanned && enchant.is(Enchantments.MENDING)) {
                // unsure if thisll work
                newEnchants.removeIf(enchantmentRegistryEntry -> enchant.is(Enchantments.MENDING));
                Lifed.LOGGER.info("heyyyy");
            }
        }

        stack.set(getComponentType(stack), newEnchants.toImmutable());
        ci.cancel();
    }
}
