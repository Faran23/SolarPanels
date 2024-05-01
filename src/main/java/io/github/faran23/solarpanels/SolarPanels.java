package io.github.faran23.solarpanels;

import com.mojang.logging.LogUtils;
import io.github.faran23.solarpanels.compat.TOP;
import io.github.faran23.solarpanels.register.Registration;
import io.github.faran23.solarpanels.solar.SolarPanelBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(SolarPanels.MODID)
public class SolarPanels {

    public static final String MODID = "solar_panels";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SolarPanels() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        Registration.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
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
                                    state.getValue(SolarPanelBlock.COLOR).getRGB() : GroupColor.fromString(Config.defaultColorName).getRGB(),
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

}
