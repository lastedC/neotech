package com.lasted.neotech.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties BISMUTH_APPLE = new FoodProperties.Builder()
            .nutrition(15)
            .saturationModifier(.9f)
            .alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 650, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 7500, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 7500, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 3000, 4), 1.0F)
            .build();

    public static final FoodProperties BISMUTH_CARROT = new FoodProperties.Builder()
            .nutrition(20)
            .saturationModifier(1F)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 250, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 3250, 0), 1.0F)
            .alwaysEdible()
            .build();
}