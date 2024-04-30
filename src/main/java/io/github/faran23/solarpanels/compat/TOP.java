package io.github.faran23.solarpanels.compat;

import io.github.faran23.solarpanels.SolarPanels;
import io.github.faran23.solarpanels.solar.SolarPanelBlockEntity;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;
import java.util.function.Function;

import static io.github.faran23.solarpanels.Utils.humanReadableNumberNoUnit;

public class TOP {

    public static void register() {
        if (!ModList.get().isLoaded("theoneprobe")) return;
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", GetTheOneProbe::new);
    }

    public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {
        public static ITheOneProbe probe;

        @Nullable
        @Override
        public Void apply(ITheOneProbe iTheOneProbe) {
            probe = iTheOneProbe;
            probe.registerProvider(new IProbeInfoProvider() {
                @Override
                public ResourceLocation getID() {
                    return new ResourceLocation(SolarPanels.MODID, "top");
                }

                @Override
                public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level,
                                         BlockState blockState, IProbeHitData data) {
                    if (level.getBlockEntity(data.getPos()) instanceof SolarPanelBlockEntity se) {
                        iProbeInfo.padding(0, 5);
                        iProbeInfo.text(Component.translatable("waila.solar_panels.generating")
                                .append(" " + humanReadableNumberNoUnit(se.getCurrentEnergyGen(), false) + "/"
                                        + humanReadableNumberNoUnit(se.getMaxEnergyGen(), false) + " FE/t"));
                        iProbeInfo.text(Component.translatable("waila.solar_panels.transferring")
                                .append(" " + humanReadableNumberNoUnit(se.getCurrentEnergyTransfer(), false) + "/"
                                        + humanReadableNumberNoUnit(se.getMaxEnergyTransfer(), false) + " FE/t"));
                    }

                }
            });
            return null;
        }
    }

}
