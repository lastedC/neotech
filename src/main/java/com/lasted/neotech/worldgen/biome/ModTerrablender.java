package com.lasted.neotech.worldgen.biome;

import com.lasted.neotech.NeoTech;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;

public class ModTerrablender {
    public static void registerBiomes() {
        Regions.register(new ModOverworldRegion(ResourceLocation.fromNamespaceAndPath(NeoTech.MODID, "overworld"), 10));
    }
}
