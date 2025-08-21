package com.lasted.neotech.item;

import com.lasted.neotech.NeoTech;
import com.lasted.neotech.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NeoTech.MODID);

    public static final Supplier<CreativeModeTab> NEOTECH_MAIN_TAB = CREATIVE_MODE_TAB.register("neotech_main",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.BISMUTH.get()))
                    .title(Component.translatable("creativetab.neotech.neotech_main"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.BISMUTH);
                        output.accept(ModItems.RAW_BISMUTH);

                        output.accept(ModBlocks.LEAD_ORE);
                        output.accept(ModItems.RAW_LEAD);
                        output.accept(ModItems.LEAD_INGOT);
                        output.accept(ModItems.LEAD_NUGGET);
                        output.accept(ModBlocks.LEAD_BLOCK);
                        output.accept(ModItems.LEAD_SWORD);
                        output.accept(ModItems.LEAD_SHOVEL);
                        output.accept(ModItems.LEAD_PICKAXE);
                        output.accept(ModItems.LEAD_AXE);
                        output.accept(ModItems.LEAD_HOE);

                        output.accept(ModItems.BISMUTH_APPLE);
                        output.accept(ModItems.BISMUTH_CARROT);

                        output.accept(ModBlocks.BISMUTH_BLOCK);
                        output.accept(ModBlocks.BISMUTH_ORE);
                        output.accept(ModBlocks.BISMUTH_DEEPSLATE_ORE);

                        output.accept(ModBlocks.PORTABLE_MINER);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}