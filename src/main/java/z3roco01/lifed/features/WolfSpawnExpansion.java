package z3roco01.lifed.features;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import z3roco01.lifed.Lifed;

/**
 * expands wolf spawning to more biomes, currently only flower forest because thats all i need
 */
public class WolfSpawnExpansion {
    public static final TagKey<Biome> EXTENDED_WOODS_TAG = TagKey.of(RegistryKeys.BIOME, Identifier.of("lifed", "is_extended_woods"));
    /**
     * Registers the biome modifications to allow their spawning
     */
    public static void registerSpawning() {
        if(Lifed.config.expandedWolfSpawning)
            registerWoodsLike(EXTENDED_WOODS_TAG);
    }

    /**
     * Registers spawning in packs similar to woods wolves
     * @param tag the tag which includes the biomes to spawn in
     */
    private static void registerWoodsLike(TagKey<Biome> tag) {
        BiomeModifications.addSpawn(BiomeSelectors.tag(tag),
                SpawnGroup.CREATURE, EntityType.WOLF, 5, 4, 4);
    }
}
