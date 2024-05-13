package io.github.faran23.solarpanels.compat;

import io.github.faran23.solarpanels.register.Registration;
import io.github.faran23.solarpanels.solar.SolarPanelBlock;
import io.github.faran23.solarpanels.solar.SolarPanelBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import static io.github.faran23.solarpanels.Utils.humanReadableNumberNoUnit;

@WailaPlugin
public class Jade implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SolarComponentProvider.INSTANCE, SolarPanelBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(SolarComponentProvider.INSTANCE, SolarPanelBlock.class);
    }

    public enum SolarComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
            if (blockAccessor.getServerData().contains("Gen") && blockAccessor.getServerData().contains("MaxGen")) {
                iTooltip.add(Component.translatable("waila.solar_panels.generating")
                        .append(": " + blockAccessor.getServerData().getString("Gen") + "/"
                                + blockAccessor.getServerData().getString("MaxGen") + " FE/t"));
            }
            if (blockAccessor.getServerData().contains("Transfer") && blockAccessor.getServerData().contains("MaxTransfer")) {
                iTooltip.add(Component.translatable("waila.solar_panels.transferring")
                        .append(": " + blockAccessor.getServerData().getString("Transfer") + "/"
                                + blockAccessor.getServerData().getString("MaxTransfer") + " FE/t"));
            }
            if (blockAccessor.getServerData().contains("Powered") && !blockAccessor.getServerData().getBoolean("Powered")) {
                iTooltip.add(Component.translatable("waila.solar_panels.not_powered")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
            }
        }

        @Override
        public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
            SolarPanelBlockEntity se = (SolarPanelBlockEntity) blockAccessor.getBlockEntity();
            tag.putString("Gen", humanReadableNumberNoUnit(se.getCurrentEnergyGen(), false));
            tag.putString("MaxGen", humanReadableNumberNoUnit(se.getMaxEnergyGen(), false));
            tag.putString("Transfer", humanReadableNumberNoUnit(se.getCurrentEnergyTransfer(), false));
            tag.putString("MaxTransfer", humanReadableNumberNoUnit(se.getMaxEnergyTransfer(), false));

            BlockState state = blockAccessor.getBlockState();
            if (state.hasProperty(BlockStateProperties.POWERED)) {
                tag.putBoolean("Powered", state.getValue(BlockStateProperties.POWERED));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return Registration.SOLAR_BLOCK.getId();
        }

    }
}
