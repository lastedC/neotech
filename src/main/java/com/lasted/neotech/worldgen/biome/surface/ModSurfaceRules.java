package com.lasted.neotech.worldgen.biome.surface;

import com.google.common.collect.ImmutableList;
import com.lasted.neotech.worldgen.biome.ModBiomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ModSurfaceRules {
    private static final SurfaceRules.RuleSource RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    public static SurfaceRules.RuleSource getRules() {
        SurfaceRules.RuleSource surfaceRules = SurfaceRules.sequence(
                makeRules()
        );

        ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();

        SurfaceRules.RuleSource surfacerules$rulesource = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), surfaceRules);
        builder.add(surfacerules$rulesource);
        return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
    }

    public static SurfaceRules.RuleSource makeRules() {
        // Conditions
        SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource isBelowWater = SurfaceRules.waterBlockCheck(-6, -1);
        
        SurfaceRules.RuleSource duneSurface = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE),
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, RED_SAND),
                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, true, 3, net.minecraft.world.level.levelgen.placement.CaveSurface.FLOOR), RED_SAND),
                RED_SANDSTONE
        );
        return SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.DUNE_DESERT), duneSurface);
    }
}
