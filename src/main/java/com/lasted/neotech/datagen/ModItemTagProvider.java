package com.lasted.neotech.datagen;

import com.lasted.neotech.NeoTech;
import com.lasted.neotech.item.ModItems;
import com.lasted.neotech.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, NeoTech.MODID, existingFileHelper);
    }

    public static final TagKey<Item> COMMON_NUGGETS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuggets"));
    public static final TagKey<Item> NUGGETS_LEAD = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuggets/lead"));
    public static final TagKey<Item> COMMON_INGOTS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots"));
    public static final TagKey<Item> INGOTS_LEAD = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/lead"));
    public static final TagKey<Item> COMMON_RAW_MATERIALS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials"));
    public static final TagKey<Item> RAW_MATERIALS_LEAD = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/lead"));

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.TRANSFORMABLE_ITEMS)
                .add(ModItems.BISMUTH.get())
                .add(ModItems.RAW_BISMUTH.get())
                .add(Items.COAL)
                .add(Items.STICK)
                .add(Items.COMPASS);

        tag(ItemTags.SWORDS)
                .add(ModItems.LEAD_SWORD.get());
        tag(ItemTags.PICKAXES)
                .add(ModItems.LEAD_PICKAXE.get());
        tag(ItemTags.SHOVELS)
                .add(ModItems.LEAD_SHOVEL.get());
        tag(ItemTags.AXES)
                .add(ModItems.LEAD_AXE.get());
        tag(ItemTags.HOES)
                .add(ModItems.LEAD_HOE.get());

        tag(COMMON_NUGGETS)
                .add(ModItems.LEAD_NUGGET.get());
        tag(NUGGETS_LEAD)
                .add(ModItems.LEAD_NUGGET.get());

        tag(COMMON_RAW_MATERIALS)
                .add(ModItems.RAW_LEAD.get());
        tag(RAW_MATERIALS_LEAD)
                .add(ModItems.RAW_LEAD.get());

        tag(COMMON_INGOTS)
                .add(ModItems.LEAD_INGOT.get());
        tag(INGOTS_LEAD)
                .add(ModItems.LEAD_INGOT.get());
    }

}