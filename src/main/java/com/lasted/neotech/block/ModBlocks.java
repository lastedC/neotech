package com.lasted.neotech.block;

import com.lasted.neotech.NeoTech;
import com.lasted.neotech.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NeoTech.MODID);

    public static final Map<String, DeferredBlock<? extends Block>> BLOCK_MAP = new HashMap<>();
    // ORES
    public static final DeferredBlock<Block> BAUXITE_ORE = registerBlock("bauxite_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of()
                    .strength(3f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            )
    );

    // MACHINES
    public static final DeferredBlock<Block> PORTABLE_MINER = registerBlock("portable_miner",
            () -> new com.lasted.neotech.block.custom.PortableMiner(BlockBehaviour.Properties.of().noOcclusion()),
            new Item.Properties().stacksTo(1));

    public static final DeferredBlock<Block> BISMUTH_BLOCK = registerBlock("bismuth_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.AMETHYST)
            )
    );

    public static final DeferredBlock<Block> BISMUTH_ORE = registerBlock("bismuth_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of()
                    .strength(3f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            )
    );
    public static final DeferredBlock<Block> BISMUTH_DEEPSLATE_ORE = registerBlock("bismuth_deepslate_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 6), BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)
            )
    );

    public static final DeferredBlock<Block> LEAD_BLOCK = registerBlock("lead_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
            )
    );
    public static final DeferredBlock<Block> LEAD_ORE = registerBlock("lead_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 6), BlockBehaviour.Properties.of()
                    .strength(2f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            )
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, new Item.Properties());
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block, Item.Properties properties) {
        DeferredBlock<T> registeredBlock = BLOCKS.register(name, block);

        ModItems.ITEMS.register(name, () -> new BlockItem(registeredBlock.get(), properties));

        BLOCK_MAP.put(name, registeredBlock);

        return registeredBlock;

//        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
//        registerBlockItem(name, toReturn);
//        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        if ("portable_miner".equals(name)) {
            ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(1)));
        } else {
            ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
