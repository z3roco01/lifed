package z3roco01.lifed.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import z3roco01.lifed.Lifed;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    /**
     * List of pvp enchantments, for limiting their level
     */
    @Unique
    private static final RegistryKey<Enchantment>[] PVP_ENCHANTS = new RegistryKey[]{
            Enchantments.BREACH,
            Enchantments.DENSITY,
            Enchantments.FIRE_ASPECT,
            Enchantments.FLAME,
            Enchantments.IMPALING,
            Enchantments.INFINITY,
            Enchantments.KNOCKBACK,
            Enchantments.LOYALTY,
            Enchantments.LUNGE,
            Enchantments.MULTISHOT,
            Enchantments.PIERCING,
            Enchantments.POWER,
            Enchantments.PUNCH,
            Enchantments.QUICK_CHARGE,
            Enchantments.RIPTIDE,
            Enchantments.SHARPNESS,
            Enchantments.SMITE,
            Enchantments.SWEEPING_EDGE,
            Enchantments.THORNS,
            Enchantments.WIND_BURST,
    };

    /**
     * All other enchantments
     */
    @Unique
    private static final RegistryKey<Enchantment>[] NON_PVP_ENCHANTS = new RegistryKey[]{
            Enchantments.AQUA_AFFINITY,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.BINDING_CURSE,
            Enchantments.BLAST_PROTECTION,
            Enchantments.DEPTH_STRIDER,
            Enchantments.EFFICIENCY,
            Enchantments.FEATHER_FALLING,
            Enchantments.FORTUNE,
            Enchantments.FROST_WALKER,
            Enchantments.LOOTING,
            Enchantments.LUCK_OF_THE_SEA,
            Enchantments.LURE,
            Enchantments.MENDING,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.PROTECTION,
            Enchantments.RESPIRATION,
            Enchantments.SILK_TOUCH,
            Enchantments.SOUL_SPEED,
            Enchantments.SWIFT_SNEAK,
            Enchantments.UNBREAKING,
            Enchantments.VANISHING_CURSE,
    };

    /**
     * Returns if the entry's key is in the pvp enchants list
     * @param entry the entry to check
     * @return true if it is in the array
     */
    @Unique
    private static boolean isPvpEnchant(RegistryEntry<Enchantment> entry) {
        for(RegistryKey<Enchantment> key : PVP_ENCHANTS)
            if(entry.matchesKey(key)) return true;

        return false;
    }

    /**
     * Returns if the entry's key is in the non pvp enchants list
     * @param entry the entry to check
     * @return true if it is in the array
     */
    @Unique
    private static boolean isNonPvpEnchant(RegistryEntry<Enchantment> entry) {
        for(RegistryKey<Enchantment> key : NON_PVP_ENCHANTS)
            if(entry.matchesKey(key)) return true;

        return false;
    }

    /**
     * Checks if an enchantment entry is considered high level ( level > 1 )
     * @param entry entry to check
     * @return true if the leve is higher than one
     */
    @Unique
    private static boolean isEnchantHighLevel(EnchantmentLevelEntry entry) {
        return entry.level() > 1;
    }

    @Inject(method = "getPossibleEntries", at = @At("RETURN"), cancellable = true)
    private static void getPossibleEntries(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        // if nothing is disallowed, then get out of here
        if(Lifed.config.highLevelOtherEnchAllowed && Lifed.config.highLevelPvpEnchAllowed) return;

        // array list that will be full of the appropriate enchants
        List<EnchantmentLevelEntry> newList = new ArrayList<>();

        for(EnchantmentLevelEntry entry : cir.getReturnValue())
            Lifed.LOGGER.info(entry.enchantment().getIdAsString() + " " + entry.level());

        for(EnchantmentLevelEntry entry : cir.getReturnValue()) {
            // dont need to even consider this entry if its not high level
            RegistryEntry<Enchantment> enchant = entry.enchantment();

            // if it is disallowed, do not add it to the lsit
            if(isEnchantHighLevel(entry) && ((!Lifed.config.highLevelPvpEnchAllowed && isPvpEnchant(enchant)) ||
                    (!Lifed.config.highLevelOtherEnchAllowed && isNonPvpEnchant(enchant))))
                continue;

            newList.add(entry);
        }

        Lifed.LOGGER.info("---------");
        for(EnchantmentLevelEntry entry : newList)
            Lifed.LOGGER.info(entry.enchantment().getIdAsString() + " " + entry.level());
        Lifed.LOGGER.info("---------");
        Lifed.LOGGER.info("---------");

        cir.setReturnValue(newList);
    }
}
