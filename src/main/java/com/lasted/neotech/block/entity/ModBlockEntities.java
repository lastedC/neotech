package com.lasted.neotech.block.entity;

import com.lasted.neotech.NeoTech;
import com.lasted.neotech.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NeoTech.MODID);

    public static final Supplier<BlockEntityType<PortableMinerBlockEntity>> PORTABLE_MINER_BE =
            BLOCK_ENTITIES.register("portable_miner_be", () -> BlockEntityType.Builder.of(
                    PortableMinerBlockEntity::new, ModBlocks.PORTABLE_MINER.get()).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
