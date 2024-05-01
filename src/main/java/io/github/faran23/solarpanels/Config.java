package io.github.faran23.solarpanels;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Mod.EventBusSubscriber(modid = SolarPanels.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // initial values
    public static final ModConfigSpec.IntValue INITIAL_GENERATION__RATE = BUILDER
            .comment("The initial rf/t energy generation rate of the solar panel. (Default: 20)")
            .defineInRange("initialGenerationRate", 20, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue INITIAL_TRANSFER_RATE = BUILDER
            .comment("The initial rf/t max energy transfer rate of the solar panel. (Default: 40)")
            .defineInRange("initialTransferRate", 40, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue INITIAL_MAX_ENERGY = BUILDER
            .comment("The initial energy capacity of the solar panel. (Default: 10000)")
            .defineInRange("initialMaxEnergy", 10000, 0, Integer.MAX_VALUE);

    // some other settings
    public static final ForgeConfigSpec.BooleanValue ALWAYS_GENERATE = BUILDER
            .comment("Should the solar panel always generate energy even when covered or during night time. (Default: false)")
            .define("alwaysGenerate", false);
    public static final ForgeConfigSpec.ConfigValue<String> DEFAULT_COLOR = BUILDER
            .comment("The default color of the solar panel (must be a valid mc color). (Default: blue)")
            .define("defaultColor", "blue", Config::validateColorName);



    // tier 1
    public static final ModConfigSpec.ConfigValue<String> TIER_1_UPGRADE_ITEM = BUILDER
            .comment("The item that represents the tier 1 upgrade. (Default: gold ingot)")
            .define("tier1UpgradeItem", "minecraft:gold_ingot", Config::validateItemName);
    public static final ModConfigSpec.IntValue TIER_1_GEN_INCREASE = BUILDER
            .comment("How much rf/t to increase the solar panel's energy generation rate when a tier 1 upgrade is applied. (Default: 20)")
            .defineInRange("tier1GenIncrease", 20, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_1_TRANSFER_INCREASE = BUILDER
            .comment("How much rf/t to increase the solar panel's max energy transfer rate when a tier 1 upgrade is applied. (Default: 40)")
            .defineInRange("tier1TransferIncrease", 40, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_1_CAPACITY_INCREASE = BUILDER
            .comment("How much rf to increase the solar panel's capacity when a tier 1 upgrade is applied. (Default: 10,000)")
            .defineInRange("tier1CapacityIncrease", 10000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> TIER_1_COLOR = BUILDER
            .comment("RGB Color of tier 1 upgrade tooltips. (Default: 255, 215, 0)")
            .defineList("tier1Color", List.of(255, 215, 0), Config::validateColor);

    // tier 2
    public static final ModConfigSpec.ConfigValue<String> TIER_2_UPGRADE_ITEM = BUILDER
            .comment("The item that represents the tier 2 upgrade. (Default: diamond)")
            .define("tier2UpgradeItem", "minecraft:diamond", Config::validateItemName);
    public static final ModConfigSpec.IntValue TIER_2_GEN_INCREASE = BUILDER
            .comment("How much rf/t to increase the solar panel's energy generation rate when a tier 2 upgrade is applied. (Default: 200)")
            .defineInRange("tier2GenIncrease", 200, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_2_TRANSFER_INCREASE = BUILDER
            .comment("How much rf/t to increase the solar panel's max energy transfer rate when a tier 2 upgrade is applied. (Default: 400)")
            .defineInRange("tier2TransferIncrease", 400, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_2_CAPACITY_INCREASE = BUILDER
            .comment("How much rf to increase the solar panel's capacity when a tier 2 upgrade is applied. (Default: 100,000)")
            .defineInRange("tier2CapacityIncrease", 100000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> TIER_2_COLOR = BUILDER
            .comment("RGB Color of tier 2 upgrade tooltips. (Default: 0, 215, 215)")
            .defineList("tier2Color", List.of(0, 215, 215), Config::validateColor);

    // tier 3
    public static final ModConfigSpec.ConfigValue<String> TIER_3_UPGRADE_ITEM = BUILDER
            .comment("The item that represents the tier 3 upgrade. (Default: netherite ingot)")
            .define("tier3UpgradeItem", "minecraft:netherite_ingot", Config::validateItemName);
    public static final ModConfigSpec.IntValue TIER_3_GEN_INCREASE = BUILDER
            .comment("How much rf/t to increase the solar panel's energy generation rate when a tier 3 upgrade is applied. (Default: 1,000)")
            .defineInRange("tier3GenIncrease", 1000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_3_TRANSFER_INCREASE = BUILDER
            .comment("How much rf/t to increase the solar panel's max energy transfer rate when a tier 3 upgrade is applied. (Default: 2,000)")
            .defineInRange("tier3TransferIncrease", 2000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_3_CAPACITY_INCREASE = BUILDER
            .comment("How much rf to increase the solar panel's capacity when a tier 3 upgrade is applied. (Default: 1,000,000)")
            .defineInRange("tier3CapacityIncrease", 1000000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> TIER_3_COLOR = BUILDER
            .comment("RGB Color of tier 3 upgrade tooltips. (Default: 50, 45, 65)")
            .defineList("tier3Color", List.of(50, 45, 65), Config::validateColor);

    // tier 4
    public static final ModConfigSpec.ConfigValue<String> TIER_4_UPGRADE_ITEM = BUILDER
            .comment("The item that represents the tier 4 upgrade. (Default: nether star)")
            .define("tier4UpgradeItem", "minecraft:nether_star", Config::validateItemName);
    public static final ModConfigSpec.IntValue TIER_4_GEN_INCREASE = BUILDER
            .comment("How much rf/t to increase the solar panel's energy generation rate when a tier 4 upgrade is applied. (Default: 10,000)")
            .defineInRange("tier4GenIncrease", 10000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_4_TRANSFER_INCREASE = BUILDER
            .comment("How much rf/t to increase the solar panel's max energy transfer rate when a tier 4 upgrade is applied. (Default: 20,000)")
            .defineInRange("tier4TransferIncrease", 20000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_4_CAPACITY_INCREASE = BUILDER
            .comment("How much rf to increase the solar panel's capacity when a tier 4 upgrade is applied. (Default: 100,000,000)")
            .defineInRange("tier4CapacityIncrease", 100000000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> TIER_4_COLOR = BUILDER
            .comment("RGB Color of tier 4 upgrade tooltips. (Default: 215, 205, 215)")
            .defineList("tier4Color", List.of(215, 205, 215), Config::validateColor);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int initialGenerationRate;
    public static int initialTransferRate;
    public static int initialMaxEnergy;

    public static Map<Item, Tier> tierMap = new HashMap<>();

    public static boolean shouldAlwayGenerate;
    public static String defaultColorName;


    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && BuiltInRegistries.ITEM.containsKey(new ResourceLocation(itemName));
    }

    private static boolean validateColorName(final Object obj) {
        return obj instanceof final String colorName && GroupColor.isColor(colorName);
    }

    private static boolean validateColor(final Object obj) {
        return obj instanceof final List<?> list && list.size() == 3
                && list.stream().allMatch(c -> c instanceof Integer && (int) c >= 0 && (int) c <= 255);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        initialGenerationRate = INITIAL_GENERATION__RATE.get();
        initialTransferRate = INITIAL_TRANSFER_RATE.get();
        initialMaxEnergy = INITIAL_MAX_ENERGY.get();

        shouldAlwayGenerate = ALWAYS_GENERATE.get();
        defaultColorName = DEFAULT_COLOR.get();

        List<? extends Integer> t1Color = TIER_1_COLOR.get();
        tierMap.put(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_1_UPGRADE_ITEM.get())),
                new Tier(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_1_UPGRADE_ITEM.get())),
                        TIER_1_GEN_INCREASE.get(), TIER_1_TRANSFER_INCREASE.get(), TIER_1_CAPACITY_INCREASE.get(),
                        new Color(t1Color.get(0), t1Color.get(1), t1Color.get(2))
                )
        );
        List<? extends Integer> t2Color = TIER_2_COLOR.get();
        tierMap.put(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_2_UPGRADE_ITEM.get())),
                new Tier(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_2_UPGRADE_ITEM.get())),
                        TIER_2_GEN_INCREASE.get(), TIER_2_TRANSFER_INCREASE.get(), TIER_2_CAPACITY_INCREASE.get(),
                        new Color(t2Color.get(0), t2Color.get(1), t2Color.get(2))
                )
        );
        List<? extends Integer> t3Color = TIER_3_COLOR.get();
        tierMap.put(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_3_UPGRADE_ITEM.get())),
                new Tier(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_3_UPGRADE_ITEM.get())),
                        TIER_3_GEN_INCREASE.get(), TIER_3_TRANSFER_INCREASE.get(), TIER_3_CAPACITY_INCREASE.get(),
                        new Color(t3Color.get(0), t3Color.get(1), t3Color.get(2))
                )
        );
        List<? extends Integer> t4Color = TIER_4_COLOR.get();
        tierMap.put(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_4_UPGRADE_ITEM.get())),
                new Tier(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_4_UPGRADE_ITEM.get())),
                        TIER_4_GEN_INCREASE.get(), TIER_4_TRANSFER_INCREASE.get(), TIER_4_CAPACITY_INCREASE.get(),
                        new Color(t4Color.get(0), t4Color.get(1), t4Color.get(2))
                )
        );
    }

    public static Tier getTier(Item item) {
        return tierMap.get(item);
    }

    public static class Tier implements Comparable<Tier> {
        public Item upgradeItem;
        public int genIncrease;
        public int transferIncrease;
        public int capacityIncrease;
        public Color colour;

        public Tier(Item upgradeItem, int genIncrease, int transferIncrease, int capacityIncrease, Color colour) {
            this.upgradeItem = upgradeItem;
            this.genIncrease = genIncrease;
            this.transferIncrease = transferIncrease;
            this.capacityIncrease = capacityIncrease;
            this.colour = colour;
        }

        @Override
        public int compareTo(@NotNull Config.Tier o) {
            return Integer.compare(this.genIncrease, o.genIncrease);
        }
    }

}
