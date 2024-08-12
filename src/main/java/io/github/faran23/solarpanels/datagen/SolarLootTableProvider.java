package io.github.faran23.solarpanels.datagen;


import io.github.faran23.solarpanels.register.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SolarLootTableProvider extends LootTableProvider {

    public SolarLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(SolarPanelBlockLootSubProvider::new, LootContextParamSets.BLOCK)));
    }

    public static class SolarPanelBlockLootSubProvider extends BlockLootSubProvider {

        public SolarPanelBlockLootSubProvider() {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return List.of(Registration.SOLAR_BLOCK.get());
        }

        @Override
        protected void generate() {
            Block panel = Registration.SOLAR_BLOCK.get();
            LootPool.Builder builder = LootPool.lootPool()
                    .when(ExplosionCondition.survivesExplosion())
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(panel)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("Energy", "BlockEntityTag.Energy", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("Gen", "BlockEntityTag.Gen", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("Transfer", "BlockEntityTag.Transfer", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("Capacity", "BlockEntityTag.Capacity", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("Color", "BlockEntityTag.Color", CopyNbtFunction.MergeStrategy.REPLACE)
                            )
                    );
            add(panel, LootTable.lootTable().withPool(builder));
        }
    }

}
