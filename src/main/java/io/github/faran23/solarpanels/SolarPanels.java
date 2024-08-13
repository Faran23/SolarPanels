package io.github.faran23.solarpanels;

import com.mojang.logging.LogUtils;
import io.github.faran23.solarpanels.compat.TOP;
import io.github.faran23.solarpanels.config.Config;
import io.github.faran23.solarpanels.config.SolarConfigScreen;
import io.github.faran23.solarpanels.datagen.SolarLootTableProvider;
import io.github.faran23.solarpanels.datagen.SolarRecipeProvider;
import io.github.faran23.solarpanels.register.Registration;
import io.github.faran23.solarpanels.solar.SolarPanelBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
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
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ModLoadingContext.get().getActiveContainer().registerExtensionPoint(IConfigScreenFactory.class, (modContainer, screen) -> new ConfigurationScreen(modContainer, screen, (SolarConfigScreen::new)));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        TOP.register();
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[Solar Panels] Hello server!");
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("[Solar Panels] Hello client!");
        }

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register((state, level, pos, tintIndex) ->
                            state.hasProperty(SolarPanelBlock.COLOR) ?
                                    state.getValue(SolarPanelBlock.COLOR).getRGB() :
                                    GroupColor.fromString(Config.defaultColorName).getRGB(),
                    Registration.SOLAR_BLOCK.get());
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register((stack, tint) -> {
                String color = stack.getOrDefault(Registration.COLOR_DATA, Config.defaultColorName);
                return GroupColor.fromString(color).getRGB();
            }, Registration.SOLAR_PANEL_ITEM.get());
        }
    }

    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, Registration.SOLAR_BE.get(), (o, dir) -> o.getEnergyHandler());
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class DataGenerators {
        @SubscribeEvent
        public static void gatherData(GatherDataEvent event) {
            DataGenerator generator = event.getGenerator();
            PackOutput output = generator.getPackOutput();
            CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

            generator.addProvider(event.includeServer(), new SolarRecipeProvider(output, lookupProvider));
            generator.addProvider(event.includeServer(), new SolarLootTableProvider(output, lookupProvider));
        }
    }
}
