package z3roco01.lifed.features;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import z3roco01.lifed.config.ConfigFiles;

/**
 * expands wolf spawning to more biomes, currently only flower forest because thats all i need
 */
public class WolfSpawnExpansion {
    public static final TagKey<Biome> EXTENDED_WOODS_TAG = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("lifed", "is_extended_woods"));
    /**
     * Registers the biome modifications to allow their spawning
     */
    public static void registerSpawning() {
        if(ConfigFiles.gameplay.expandedWolfSpawning)
            registerWoodsLike(EXTENDED_WOODS_TAG);
    }

    /**
     * Registers spawning in packs similar to woods wolves
     * @param tag the tag which includes the biomes to spawn in
     */
    private static void registerWoodsLike(TagKey<Biome> tag) {
        BiomeModifications.addSpawn(BiomeSelectors.tag(tag),
                MobCategory.CREATURE, EntityType.WOLF, 5, 4, 4);
    }
}
