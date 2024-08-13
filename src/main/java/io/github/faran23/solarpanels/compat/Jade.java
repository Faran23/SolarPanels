package io.github.faran23.solarpanels.compat;

import io.github.faran23.solarpanels.config.Config;
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
            CompoundTag serverData = blockAccessor.getServerData();
            if (serverData.contains("Gen") && serverData.contains("MaxGen")) {
                iTooltip.add(Component.translatable("waila.solar_panels.generating")
                        .append(": " + serverData.getString("Gen") + "/"
                                + serverData.getString("MaxGen") + " FE/t"));
            }
            if (serverData.contains("Transfer") && serverData.contains("MaxTransfer")) {
                iTooltip.add(Component.translatable("waila.solar_panels.transferring")
                        .append(": " + serverData.getString("Transfer") + "/"
                                + serverData.getString("MaxTransfer") + " FE/t"));
            }

            if (serverData.contains("MaxGenInt") && serverData.getInt("MaxGenInt") >= Config.maxGenerationRate) {
                iTooltip.add(Component.translatable("waila.solar_panels.at_max_gen")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
            }
            if (serverData.contains("MaxTransferInt") && serverData.getInt("MaxTransferInt") >= Config.maxTransferRate) {
                iTooltip.add(Component.translatable("waila.solar_panels.at_max_transfer")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
            }
            if (serverData.contains("Capacity") && serverData.getInt("Capacity") >= Config.maxCapacity) {
                iTooltip.add(Component.translatable("waila.solar_panels.at_max_capacity")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
            }

            if (serverData.contains("Powered") && !serverData.getBoolean("Powered")) {
                iTooltip.add(Component.translatable("waila.solar_panels.not_powered")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
            }
        }

        @Override
        public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
            SolarPanelBlockEntity se = (SolarPanelBlockEntity) blockAccessor.getBlockEntity();
            tag.putString("Gen", humanReadableNumberNoUnit(se.getCurrentEnergyGen(), false));
            tag.putString("MaxGen", humanReadableNumberNoUnit(se.getMaxEnergyGen(), false));
            tag.putInt("MaxGenInt", se.getMaxEnergyGen());
            tag.putString("Transfer", humanReadableNumberNoUnit(se.getCurrentEnergyTransfer(), false));
            tag.putString("MaxTransfer", humanReadableNumberNoUnit(se.getMaxEnergyTransfer(), false));
            tag.putInt("MaxTransferInt", se.getMaxEnergyTransfer());
            tag.putInt("Capacity", se.getMaxEnergy());

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
