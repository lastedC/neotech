package com.lasted.neotech.worldgen.biome.surface;

import com.lasted.neotech.worldgen.biome.ModBiomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ModSurfaceRules {
    private static final SurfaceRules.RuleSource RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.RuleSource duneSurface = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE),
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, RED_SAND),
                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, true, 3, net.minecraft.world.level.levelgen.placement.CaveSurface.FLOOR), RED_SAND),
                RED_SANDSTONE
        );
        return SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.DUNE_DESERT), duneSurface);
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
