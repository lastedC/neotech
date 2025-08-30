package com.lasted.neotech.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PortableMiningManager implements PreparableReloadListener {
    public static final PortableMiningManager INSTANCE = new PortableMiningManager();
    private static final String FOLDER = "portable_mining"; // data/<namespace>/portable_mining/*.json

    /**
     * @param blockTag nullable
     * @param blocks   may be empty
     */
    public record Entry(boolean notPortable, List<net.minecraft.world.item.crafting.Ingredient> ingredients, List<ItemStack> results, TagKey<Block> blockTag, Set<Block> blocks, ItemStack result) {
        public boolean matches(BlockState state) {
            // New format: ingredients list (items/tags). We match by comparing the block's item form.
            if (ingredients != null && !ingredients.isEmpty()) {
                Item blockItem = state.getBlock().asItem();
                if (blockItem != null && blockItem != net.minecraft.world.item.Items.AIR) {
                    ItemStack probe = new ItemStack(blockItem);
                    for (net.minecraft.world.item.crafting.Ingredient ing : ingredients) {
                        if (ing.test(probe)) return true;
                    }
                }
            }
            // Legacy format: tag/blocks
            if (blockTag != null && state.is(blockTag)) return true;
            if (blocks != null && !blocks.isEmpty()) {
                for (Block b : blocks) if (state.is(b)) return true;
            }
            return false;
        }
    }

    private final List<Entry> entries = new ArrayList<>();
    private final Set<Item> allowedResultItems = new HashSet<>();

    private PortableMiningManager() {
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager,
                                          ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
                                          Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, JsonObject> parsed = new HashMap<>();
            try {
                Map<ResourceLocation, Resource> resources = resourceManager.listResources(FOLDER, rl -> rl.getPath().endsWith(".json"));
                for (Map.Entry<ResourceLocation, Resource> e : resources.entrySet()) {
                    try (Reader reader = e.getValue().openAsReader()) {
                        JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                        parsed.put(e.getKey(), obj);
                    } catch (Exception ex) {
                        // Ignore malformed entries to avoid crashing datapack reload
                    }
                }
            } catch (Exception ex) {
                // Ignore unexpected issues during listing/reading resources
            }
            return parsed;
        }, backgroundExecutor).thenCompose(barrier::wait).thenAcceptAsync(parsed -> {
            applyParsed((Map<ResourceLocation, JsonObject>) parsed);
        }, gameExecutor);
    }

    private void applyParsed(Map<ResourceLocation, JsonObject> map) {
        synchronized (this) {
            entries.clear();
            allowedResultItems.clear();
            for (Map.Entry<ResourceLocation, JsonObject> e : map.entrySet()) {
                try {
                    JsonObject json = e.getValue();
                    boolean notPortable = json.has("notPortable") && json.get("notPortable").getAsBoolean();
                    // parse results (array) with fallback to single
                    List<ItemStack> results = new ArrayList<>();
                    if (json.has("result") && json.get("result").isJsonArray()) {
                        JsonArray arr = json.getAsJsonArray("result");
                        for (int i = 0; i < arr.size(); i++) {
                            JsonObject r = arr.get(i).getAsJsonObject();
                            ResourceLocation itemId = ResourceLocation.parse(r.get("item").getAsString());
                            int count = r.has("count") ? r.get("count").getAsInt() : 1;
                            results.add(new ItemStack(Objects.requireNonNull(BuiltInRegistries.ITEM.get(itemId)), count));
                        }
                    } else {
                        ItemStack single = ioResult(json);
                        if (!single.isEmpty()) results.add(single);
                    }

                    // parse ingredients (array), supports {item} or {tag}
                    List<net.minecraft.world.item.crafting.Ingredient> ingredients = new ArrayList<>();
                    if (json.has("ingredients")) {
                        JsonArray arr = json.getAsJsonArray("ingredients");
                        for (int i = 0; i < arr.size(); i++) {
                            JsonObject ing = arr.get(i).getAsJsonObject();
                            if (ing.has("item")) {
                                ResourceLocation id = ResourceLocation.parse(ing.get("item").getAsString());
                                Item item = BuiltInRegistries.ITEM.get(id);
                                if (item == null) throw new IllegalArgumentException("Unknown item: " + id);
                                ingredients.add(net.minecraft.world.item.crafting.Ingredient.of(new ItemStack(item)));
                            } else if (ing.has("tag")) {
                                ResourceLocation tagId = ResourceLocation.parse(ing.get("tag").getAsString());
                                TagKey<Item> itemTag = TagKey.create(BuiltInRegistries.ITEM.key(), tagId);
                                ingredients.add(net.minecraft.world.item.crafting.Ingredient.of(itemTag));
                            }
                        }
                    }

                    // legacy support
                    TagKey<Block> tag = null;
                    Set<Block> blocks = new HashSet<>();
                    if (json.has("block_tag")) {
                        ResourceLocation tagId = ResourceLocation.parse(json.get("block_tag").getAsString());
                        tag = TagKey.create(BuiltInRegistries.BLOCK.key(), tagId);
                    }
                    if (json.has("blocks")) {
                        JsonArray arr = json.getAsJsonArray("blocks");
                        for (int i = 0; i < arr.size(); i++) {
                            ResourceLocation id = ResourceLocation.parse(arr.get(i).getAsString());
                            Block b = BuiltInRegistries.BLOCK.get(id);
                            if (b == null) throw new IllegalArgumentException("Unknown block: " + id);
                            blocks.add(b);
                        }
                    }
                    if (ingredients.isEmpty() && tag == null && blocks.isEmpty()) {
                        throw new IllegalArgumentException("portable_mining entry must define 'ingredients' or legacy 'block_tag'/'blocks'");
                    }
                    // choose primary result for legacy APIs
                    ItemStack result = results.isEmpty() ? ItemStack.EMPTY : results.get(0);
                    entries.add(new Entry(notPortable, ingredients, results, tag, blocks, result));
                    for (ItemStack it : results) if (!it.isEmpty()) allowedResultItems.add(it.getItem());
                } catch (Exception ex) {
                    // Skip invalid entries
                }
            }
        }
    }

    private static ItemStack ioResult(JsonObject json) {
        ItemStack result;
        if (json.has("result") && json.get("result").isJsonObject()) {
            JsonObject r = json.getAsJsonObject("result");
            ResourceLocation itemId = ResourceLocation.parse(r.get("item").getAsString());
            int count = r.has("count") ? r.get("count").getAsInt() : 1;
            result = new ItemStack(Objects.requireNonNull(BuiltInRegistries.ITEM.get(itemId)), count);
        } else {
            // legacy: item/count at root
            ResourceLocation itemId = ResourceLocation.parse(json.get("item").getAsString());
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            result = new ItemStack(Objects.requireNonNull(BuiltInRegistries.ITEM.get(itemId)), count);
        }
        return result;
    }

    public synchronized Entry findMatch(BlockState state) {
        for (Entry e : entries) if (!e.notPortable() && e.matches(state)) return e;
        return null;
    }

    public synchronized boolean isAllowedResult(ItemStack stack) {
        return !stack.isEmpty() && allowedResultItems.contains(stack.getItem());
    }

    public synchronized java.util.List<Entry> getEntries() {
        return java.util.List.copyOf(entries);
    }
}
