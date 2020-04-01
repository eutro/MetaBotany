package eutros.botaniapp.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eutros.botaniapp.common.block.BotaniaPPBlocks;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.SurvivesExplosion;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class BlockLootProvider implements IDataProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;

    private final Map<Block, Function<Block, LootTable.Builder>> functionTable = new HashMap<>();

    public BlockLootProvider(DataGenerator generator) {
        this.generator = generator;

        functionTable.put(BotaniaPPBlocks.poweredAir, BlockLootProvider::empty);
    }

    private static Path getPath(Path root, ResourceLocation id) {
        return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
    }

    private static LootTable.Builder genRegular(Block b) {
        LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
        LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry)
                .acceptCondition(SurvivesExplosion.builder());
        return LootTable.builder().addLootPool(pool);
    }

    private static LootTable.Builder empty(Block b) {
        return LootTable.builder();
    }

    @Override
    public void act(@NotNull DirectoryCache cache) throws IOException {
        Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

        for(Block b : ForgeRegistries.BLOCKS) {
            if(!Reference.MOD_ID.equals(Objects.requireNonNull(b.getRegistryName()).getNamespace()))
                continue;
            Function<Block, LootTable.Builder> func = functionTable.getOrDefault(b, BlockLootProvider::genRegular);
            tables.put(b.getRegistryName(), func.apply(b));
        }

        for(Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
            Path path = getPath(generator.getOutputFolder(), e.getKey());
            IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().setParameterSet(LootParameterSets.BLOCK).build()), path);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return "BotaniaPP block loot tables";
    }

}
