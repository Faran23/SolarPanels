package io.github.faran23.solarpanels.solar;

import io.github.faran23.solarpanels.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.faran23.solarpanels.Utils.humanReadableNumberNoUnit;

public class SolarPanelItem extends BlockItem {

    public SolarPanelItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> componentList, TooltipFlag flag) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag == null) return;
            CompoundTag beTag = tag.getCompound("BlockEntityTag");

            int energy = 0;
            int gen = Config.initialGenerationRate;
            int transfer = Config.initialTransferRate;
            int capacity = Config.initialMaxEnergy;

            if (beTag.contains("Energy")) {
                Tag energyTag = beTag.get("Energy");
                if (energyTag instanceof IntTag iTag) {
                    energy = iTag.getAsInt();
                }
            }
            if (beTag.contains("Gen")) {
                gen = beTag.getInt("Gen");
            }
            if (beTag.contains("Transfer")) {
                transfer = beTag.getInt("Transfer");
            }
            if (beTag.contains("Capacity")) {
                capacity = beTag.getInt("Capacity");
            }

            // nbt values
            componentList.add(Component.empty());
            componentList.add(Component.translatable("tooltip.solar_panels.energy")
                    .append(Component.literal(": " + humanReadableNumberNoUnit(energy, false) + "/" + humanReadableNumberNoUnit(capacity, false) + " FE"))
                    .withStyle(ChatFormatting.BLUE));
            componentList.add(Component.translatable("tooltip.solar_panels.generation")
                    .append(Component.literal(": " + humanReadableNumberNoUnit(gen, false) + " FE/t"))
                    .withStyle(ChatFormatting.BLUE));
            componentList.add(Component.translatable("tooltip.solar_panels.transfer")
                    .append(Component.literal(": " + humanReadableNumberNoUnit(transfer, false) + " FE/t"))
                    .withStyle(ChatFormatting.BLUE));


            // upgrade info
            if (Screen.hasShiftDown()) {
                boolean details = Screen.hasControlDown();

                componentList.add(Component.empty());
                componentList.add(Component.translatable("tooltip.solar_panels.upgrade").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_RED));
                for (Config.Tier tier : Config.tierMap.values().stream().sorted().toList()) {
                    componentList.add(tier.upgradeItem.getDefaultInstance().getHoverName().copy().withStyle(Style.EMPTY.withColor(tier.colour.getRGB())).append(
                            details ? Component.literal(": [" + humanReadableNumberNoUnit(tier.genIncrease, false) + ", " + humanReadableNumberNoUnit(tier.transferIncrease, false) + ", " + humanReadableNumberNoUnit(tier.capacityIncrease, false) + "]")
                                    : Component.empty()
                    ));
                }

                componentList.add(Component.empty());
                if (!details) {
                    componentList.add(Component.translatable("tooltip.solar_panels.ctrl_info",
                            Component.literal("[CTRL]").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD)));
                } else {
                    componentList.add(Component.translatable("tooltip.solar_panels.upgrade_item").append(": [")
                            .append(Component.translatable("tooltip.solar_panels.upgrade_gen")).append(", ")
                            .append(Component.translatable("tooltip.solar_panels.upgrade_transfer")).append(", ")
                            .append(Component.translatable("tooltip.solar_panels.upgrade_capacity")).append("]")
                            .withStyle(ChatFormatting.BOLD, ChatFormatting.RED));
                }
            } else {
                componentList.add(Component.empty());
                componentList.add(Component.translatable("tooltip.solar_panels.shift_info",
                        Component.literal("[SHIFT]").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD)));
            }
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        setupTag(tag);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        if (!stack.hasTag()) {
            setupTag(stack.getOrCreateTag());
        }
    }

    private CompoundTag setupTag(CompoundTag tag) {
        tag.put("Energy", IntTag.valueOf(0));
        tag.putInt("Gen", Config.initialGenerationRate);
        tag.putInt("Transfer", Config.initialTransferRate);
        tag.putInt("Capacity", Config.initialMaxEnergy);
        return tag;
    }
}
