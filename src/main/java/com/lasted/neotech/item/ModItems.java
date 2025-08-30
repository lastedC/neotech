package com.lasted.neotech.item;

import com.lasted.neotech.NeoTech;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NeoTech.MODID);

    public static final Map<String, DeferredItem<Item>> ITEM_MAP = new HashMap<>();
    // TUTORIAL STUFF
    public static final DeferredItem<Item> BISMUTH = ITEMS.register("bismuth",
            () -> new Item(new Item.Properties())
    );
    // Sword
    public static final DeferredItem<SwordItem> LEAD_SWORD = ITEMS.register("lead_sword",
            () -> new SwordItem(ModToolTiers.LEAD, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.LEAD, 5, -2.4f))));
    // Pickaxe
    public static final DeferredItem<PickaxeItem> LEAD_PICKAXE = ITEMS.register("lead_pickaxe",
            () -> new PickaxeItem(ModToolTiers.LEAD, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.LEAD, 5, -2.4f))));
    public static final DeferredItem<Item> RAW_BISMUTH = ITEMS.register("raw_bismuth",
            () -> new Item(new Item.Properties())
    );
    public static final DeferredItem<Item> BISMUTH_APPLE = ITEMS.register("bismuth_apple",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BISMUTH_APPLE)));
    public static final DeferredItem<Item> BISMUTH_CARROT = ITEMS.register("bismuth_carrot",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BISMUTH_CARROT)));

    public static final DeferredItem<Item> RAW_LEAD = ITEMS.register("raw_lead",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.register("lead_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAD_NUGGET = ITEMS.register("lead_nugget",
            () -> new Item(new Item.Properties()));
    // Shovel
    public static final DeferredItem<ShovelItem> LEAD_SHOVEL = ITEMS.register("lead_shovel",
            () -> new ShovelItem(ModToolTiers.LEAD, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.LEAD, 5, -2.4f))));
    // Axe
    public static final DeferredItem<AxeItem> LEAD_AXE = ITEMS.register("lead_axe",
            () -> new AxeItem(ModToolTiers.LEAD, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.LEAD, 5, -2.4f))));
    // Hoe
    public static final DeferredItem<HoeItem> LEAD_HOE = ITEMS.register("lead_hoe",
            () -> new HoeItem(ModToolTiers.LEAD, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.LEAD, 5, -2.4f))));
    private static final String[] ITEM_NAMES = {
            "bauxite",
            "raw_caterium", "caterium_ingot",
            "limestone",
            "sulfur",
            "uranium"
    };

    static {
        for (String name : ITEM_NAMES) {
            ITEM_MAP.put(name, ITEMS.register(name, () -> new Item(new Item.Properties())));
        }
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}