package com.lasted.neotech.item;

import com.lasted.neotech.NeoTech;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NeoTech.MODID);

    public static final DeferredItem<Item> RUNESTONE = ITEMS.register("blank_runestone",
            () -> new Item(new Item.Properties())
    );
    public static final DeferredItem<Item> BISMUTH = ITEMS.register("bismuth",
            () -> new Item(new Item.Properties())
    );
    public static final DeferredItem<Item> RAW_BISMUTH = ITEMS.register("raw_bismuth",
            () -> new Item(new Item.Properties())
    );
    public static final DeferredItem<Item> BISMUTH_APPLE = ITEMS.register("bismuth_apple",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BISMUTH_APPLE)));
    public static final DeferredItem<Item> BISMUTH_CARROT = ITEMS.register("bismuth_carrot",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BISMUTH_CARROT)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}