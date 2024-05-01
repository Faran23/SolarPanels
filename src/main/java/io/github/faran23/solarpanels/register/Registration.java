package io.github.faran23.solarpanels.register;

import io.github.faran23.solarpanels.SolarPanels;
import io.github.faran23.solarpanels.solar.SolarPanelBlock;
import io.github.faran23.solarpanels.solar.SolarPanelBlockEntity;
import io.github.faran23.solarpanels.solar.SolarPanelItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Registration {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SolarPanels.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SolarPanels.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SolarPanels.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SolarPanels.MODID);


    public static final DeferredBlock<SolarPanelBlock> SOLAR_BLOCK = BLOCKS.register("solar_panel", SolarPanelBlock::new);
    public static final Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_BE =
            BLOCK_ENTITIES.register("solar_panel", () ->
                    BlockEntityType.Builder.of(SolarPanelBlockEntity::new, SOLAR_BLOCK.get()).build(null));
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM = ITEMS.register("solar_panel", () ->
            new SolarPanelItem(SOLAR_BLOCK.get(), new Item.Properties().fireResistant()));

    public static final Supplier<CreativeModeTab> SOLAR_CREATIVE_TAB = CREATIVE_TABS.register("solar_panels", () ->
            CreativeModeTab.builder().icon(() -> SOLAR_PANEL_ITEM.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.solar_panels"))
                    .displayItems((par, out) -> ITEMS.getEntries().forEach(item -> out.accept(item.get()))).build());

     public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ITEMS.register(bus);
        CREATIVE_TABS.register(bus);
     }
}
