package com.lasted.neotech.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    public static class Entry {
        public final TagKey<Block> blockTag; // nullable
        public final Set<Block> blocks;      // may be empty
        public final ItemStack result;
        public Entry(TagKey<Block> blockTag, Set<Block> blocks, ItemStack result) {
            this.blockTag = blockTag;
            this.blocks = blocks;
            this.result = result;
        }
        public boolean matches(BlockState state) {
            if (blockTag != null && state.is(blockTag)) return true;
            if (blocks != null && !blocks.isEmpty()) {
                for (Block b : blocks) if (state.is(b)) return true;
            }
            return false;
        }
    }

    private final List<Entry> entries = new ArrayList<>();
    private final Set<Item> allowedResultItems = new HashSet<>();

    private PortableMiningManager() {}

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
                    // parse result
                    ItemStack result = ioResult(json);
                    // parse inputs
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
                    if (tag == null && blocks.isEmpty()) {
                        throw new IllegalArgumentException("portable_mining entry must define 'block_tag' or non-empty 'blocks'");
                    }
                    entries.add(new Entry(tag, blocks, result));
                    if (!result.isEmpty()) allowedResultItems.add(result.getItem());
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
        for (Entry e : entries) if (e.matches(state)) return e;
        return null;
    }

    public synchronized boolean isAllowedResult(ItemStack stack) {
        return !stack.isEmpty() && allowedResultItems.contains(stack.getItem());
    }

    public synchronized java.util.List<Entry> getEntries() {
        return java.util.List.copyOf(entries);
    }
}
