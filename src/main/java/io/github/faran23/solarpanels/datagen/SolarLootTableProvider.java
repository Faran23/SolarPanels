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
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SolarLootTableProvider extends LootTableProvider {

    public SolarLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(SolarPanelBlockLootSubProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider);
    }

    public static class SolarPanelBlockLootSubProvider extends BlockLootSubProvider {

        public SolarPanelBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
        }

        @Override
        @NotNull
        protected Iterable<Block> getKnownBlocks() {
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
                            .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                    .include(Registration.SOLAR_ENERGY_DATA.get())
                                    .include(Registration.COLOR_DATA.get())
                            )
                    );
            add(panel, LootTable.lootTable().withPool(builder));
        }
    }

}
