package com.lasted.neotech.worldgen.biome;

import com.lasted.neotech.NeoTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import javax.annotation.Nullable;

public class ModBiomes {
    public static final ResourceKey<Biome> DUNE_DESERT = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(NeoTech.MODID, "dune_desert"));

    public static void bootstrap(BootstrapContext<Biome> context) {
        context.register(DUNE_DESERT, duneDesert(context));
    }

    private static void addFeature(BiomeGenerationSettings.Builder biomeBuilder, GenerationStep.Decoration step, ResourceKey<PlacedFeature> feature) {
        biomeBuilder.addFeature(step, feature);
    }

    private static Biome biomeWithColourOverrides(boolean hasPrecipitation, float temperature, float downfall, int grassColour, int foliageColour, int fogColour,
                                                 int skyColour, int waterFogColour, int waterColour, MobSpawnSettings.Builder spawnBuilder,
                                                  BiomeGenerationSettings.Builder biomeBuilder, @Nullable Music music) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(hasPrecipitation)
                .downfall(downfall)
                .temperature(temperature)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(waterColour)
                        .waterFogColor(waterFogColour)
                        .skyColor(skyColour)
                        .grassColorOverride(grassColour)
                        .foliageColorOverride(foliageColour)
                        .fogColor(fogColour)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }

    private static void globalOverworldGeneration(BiomeGenerationSettings.Builder biomeBuilder) {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(biomeBuilder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(biomeBuilder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSprings(biomeBuilder);
        BiomeDefaultFeatures.addSurfaceFreezing(biomeBuilder);
    }

    private static Biome duneDesert(BootstrapContext<Biome> context) {
        // Mob spawns
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);
        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.ARMADILLO, 3, 1, 2));

        // Biome Features
        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        // Vegetation
        addFeature(biomeBuilder, GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_PLAINS);

        return biomeWithColourOverrides(false, 0.0F, 0.0F, 0xe5d27f, 0xc2b280, 0xe6d3a1, 0xffd580, 0x050533, 0x3f76e4, spawnBuilder, biomeBuilder, null);
    }
}
