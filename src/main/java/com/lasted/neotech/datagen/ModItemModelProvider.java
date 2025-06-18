package com.lasted.neotech.datagen;

import com.lasted.neotech.NeoTech;
import com.lasted.neotech.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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

//        basicItem(ModItems.STARLIGHT_ASHES.get());
//        basicItem(ModItems.CHISEL.get());
    }
}