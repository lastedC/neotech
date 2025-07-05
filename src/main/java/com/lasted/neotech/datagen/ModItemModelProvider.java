package com.lasted.neotech.datagen;

import com.lasted.neotech.NeoTech;
import com.lasted.neotech.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NeoTech.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.BISMUTH.get());
        basicItem(ModItems.RAW_BISMUTH.get());
        basicItem(ModItems.BISMUTH_APPLE.get());
        basicItem(ModItems.BISMUTH_CARROT.get());

        basicItem(ModItems.RAW_LEAD.get());
        basicItem(ModItems.LEAD_INGOT.get());
        basicItem(ModItems.LEAD_NUGGET.get());

        handheldItem(ModItems.LEAD_SWORD);
        handheldItem(ModItems.LEAD_PICKAXE);
        handheldItem(ModItems.LEAD_AXE);
        handheldItem(ModItems.LEAD_SHOVEL);
        handheldItem(ModItems.LEAD_AXE);
        handheldItem(ModItems.LEAD_HOE);
    }

    private ItemModelBuilder handheldItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(NeoTech.MODID, "item/" + item.getId().getPath()));
    }
}