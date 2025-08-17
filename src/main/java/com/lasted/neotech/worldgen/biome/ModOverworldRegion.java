package com.lasted.neotech.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

public class ModOverworldRegion extends Region {
    public ModOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, modifiedVanillaOverworldBuilder -> {
            // Broaden placement: hot, dry, near-inland to inland, and flatter erosion to improve frequency.
            VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
            new ParameterUtils.ParameterPointListBuilder()
                    .temperature(ParameterUtils.Temperature.HOT)
                    .humidity(ParameterUtils.Humidity.DRY)
                    .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.NEAR_INLAND, ParameterUtils.Continentalness.INLAND))
                    .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_3, ParameterUtils.Erosion.EROSION_5))
                    .depth(ParameterUtils.Depth.SURFACE, ParameterUtils.Depth.FLOOR)
                    .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                    .build().forEach(point -> builder.add(point, ModBiomes.DUNE_DESERT));
            builder.build().forEach(mapper);
        });
    }
}
