package io.github.faran23.solarpanels;

import com.mojang.logging.LogUtils;
import io.github.faran23.solarpanels.compat.TOP;
import io.github.faran23.solarpanels.datagen.SolarLootTableProvider;
import io.github.faran23.solarpanels.datagen.SolarRecipeProvider;
import io.github.faran23.solarpanels.register.Registration;
import io.github.faran23.solarpanels.solar.SolarPanelBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(SolarPanels.MODID)
public class SolarPanels {

    public static final String MODID = "solar_panels";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SolarPanels(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onRegisterCapabilities);
        Registration.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        TOP.register();
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[Solar Panels] Hello server!");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("[Solar Panels] Hello client!");
        }

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            event.getBlockColors().register((state, level, pos, tintIndex) ->
                            state.hasProperty(SolarPanelBlock.COLOR) ?
                                    state.getValue(SolarPanelBlock.COLOR).getRGB() :
                                    GroupColor.fromString(Config.defaultColorName).getRGB(),
                    Registration.SOLAR_BLOCK.get());
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.getItemColors().register((stack, tint) -> {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("BlockEntityTag")) {
                    String color = ((CompoundTag) tag.get("BlockEntityTag")).getString("Color");
                    return GroupColor.fromString(color).getColor().getRGB();
                }
                return GroupColor.fromString(Config.defaultColorName).getRGB();
            }, Registration.SOLAR_PANEL_ITEM.get());
        }
    }

    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, Registration.SOLAR_BE.get(), (o, dir) -> o.getEnergyHandler());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class DataGenerators {
        @SubscribeEvent
        public static void gatherData(GatherDataEvent event) {
            DataGenerator generator = event.getGenerator();
            PackOutput output = generator.getPackOutput();

            generator.addProvider(event.includeServer(), new SolarRecipeProvider(output));
            generator.addProvider(event.includeServer(), new SolarLootTableProvider(output));
        }
    }

}
