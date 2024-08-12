package io.github.faran23.solarpanels;

import com.electronwill.nightconfig.core.EnumGetMethod;
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
    private static final List<String> ALL_ITEMS = BuiltInRegistries.ITEM.keySet().stream().map(ResourceLocation::toString).toList();
    private static final int MAX_RGB = 16777215;
    private static final String DEFAULT_STRING = " (Default: %s)";
    private static final String UPGRADE_ITEM_COMMENT = "The item that represents the upgrade.";
    private static final String GEN_INCREASE_COMMENT = "How much FE/t to increase the solar panel's energy generation rate by when the upgrade is applied.";
    private static final String TRANSFER_INCREASE_COMMENT = "How much FE/t to increase the solar panel's max energy transfer rate by when the upgrade is applied.";
    private static final String CAPACITY_INCREASE_COMMENT = "How much FE to increase the solar panel's capacity by when the upgrade is applied.";
    private static final String COLOR_COMMENT = "RGB Color of the upgrade tooltip.";

    // some other settings
    public static final ModConfigSpec.BooleanValue ALWAYS_GENERATE = BUILDER
            .comment("Whether the solar panel should always generate energy even when covered or during night time.", java.lang.String.format(DEFAULT_STRING, false))
            .define("always_generate", false);
    public static final ModConfigSpec.EnumValue<GroupColor> DEFAULT_COLOR = BUILDER
            .comment("The default color of the solar panel.", java.lang.String.format(DEFAULT_STRING, "blue"))
            .defineEnum("default_color", GroupColor.BLUE, EnumGetMethod.NAME_IGNORECASE);

    // initial values
    public static final ModConfigSpec.IntValue INITIAL_GENERATION_RATE = BUILDER
            .comment("The initial FE/t energy generation rate of the solar panel.", java.lang.String.format(DEFAULT_STRING, 20))
            .defineInRange("initial_generation_rate", 20, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue INITIAL_TRANSFER_RATE = BUILDER
            .comment("The initial FE/t max energy transfer rate of the solar panel.", java.lang.String.format(DEFAULT_STRING, 40))
            .defineInRange("initial_transfer_rate", 40, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue INITIAL_CAPACITY = BUILDER
            .comment("The initial FE energy capacity of the solar panel.", java.lang.String.format(DEFAULT_STRING, 10000))
            .defineInRange("initial_capacity", 10000, 0, Integer.MAX_VALUE);

    // max values
    public static final ModConfigSpec.IntValue MAX_GENERATION_RATE = BUILDER
            .comment("The maximum FE/t energy generation rate that the solar panel can be upgraded to.", java.lang.String.format(DEFAULT_STRING, Integer.MAX_VALUE))
            .defineInRange("max_generation_rate", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue MAX_TRANSFER_RATE = BUILDER
            .comment("The maximum FE/t max energy transfer rate that the solar panel can be upgraded to.", java.lang.String.format(DEFAULT_STRING, Integer.MAX_VALUE))
            .defineInRange("max_transfer_rate", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue MAX_CAPACITY = BUILDER
            .comment("The maximum FE energy capacity that the solar panel can be upgraded to.", java.lang.String.format(DEFAULT_STRING, Integer.MAX_VALUE))
            .defineInRange("max_capacity", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

    // tier 1
    public static final ModConfigSpec.ConfigValue<String> TIER_1_UPGRADE_ITEM = BUILDER
            .comment(UPGRADE_ITEM_COMMENT, java.lang.String.format(DEFAULT_STRING, "gold_ingot"))
            .defineInList("tier1.upgrade_item", "minecraft:gold_ingot", ALL_ITEMS);
    public static final ModConfigSpec.IntValue TIER_1_GEN_INCREASE = BUILDER
            .comment(GEN_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 20))
            .defineInRange("tier1.gen_increase", 20, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_1_TRANSFER_INCREASE = BUILDER
            .comment(TRANSFER_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 40))
            .defineInRange("tier1.transfer_increase", 40, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_1_CAPACITY_INCREASE = BUILDER
            .comment(CAPACITY_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 10000))
            .defineInRange("tier1.capacity_increase", 10000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_1_COLOR = BUILDER
            .comment(COLOR_COMMENT, java.lang.String.format(DEFAULT_STRING, 16766720))
            .defineInRange("tier1.tooltip_color", 16766720, 0, MAX_RGB);

    // tier 2
    public static final ModConfigSpec.ConfigValue<String> TIER_2_UPGRADE_ITEM = BUILDER
            .comment(UPGRADE_ITEM_COMMENT, java.lang.String.format(DEFAULT_STRING, "diamond"))
            .defineInList("tier2.upgrade_item", "minecraft:diamond", ALL_ITEMS);
    public static final ModConfigSpec.IntValue TIER_2_GEN_INCREASE = BUILDER
            .comment(GEN_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 200))
            .defineInRange("tier2.gen_increase", 200, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_2_TRANSFER_INCREASE = BUILDER
            .comment(TRANSFER_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 400))
            .defineInRange("tier2.transfer_increase", 400, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_2_CAPACITY_INCREASE = BUILDER
            .comment(CAPACITY_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 100000))
            .defineInRange("tier2.capacity_increase", 100000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_2_COLOR = BUILDER
            .comment(COLOR_COMMENT, java.lang.String.format(DEFAULT_STRING, 55255))
            .defineInRange("tier2.color", 55255, 0, MAX_RGB);

    // tier 3
    public static final ModConfigSpec.ConfigValue<String> TIER_3_UPGRADE_ITEM = BUILDER
            .comment(UPGRADE_ITEM_COMMENT, java.lang.String.format(DEFAULT_STRING, "netherite_ingot"))
            .defineInList("tier3.upgrade_item", "minecraft:netherite_ingot", ALL_ITEMS);
    public static final ModConfigSpec.IntValue TIER_3_GEN_INCREASE = BUILDER
            .comment(GEN_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 1000))
            .defineInRange("tier3.gen_increase", 1000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_3_TRANSFER_INCREASE = BUILDER
            .comment(TRANSFER_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 2000))
            .defineInRange("tier3.transfer_increase", 2000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_3_CAPACITY_INCREASE = BUILDER
            .comment(CAPACITY_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 1000000))
            .defineInRange("tier3.capacity_increase", 1000000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_3_COLOR = BUILDER
            .comment(COLOR_COMMENT, java.lang.String.format(DEFAULT_STRING, 3288385))
            .defineInRange("tier3.color", 3288385, 0, MAX_RGB);

    // tier 4
    public static final ModConfigSpec.ConfigValue<String> TIER_4_UPGRADE_ITEM = BUILDER
            .comment(UPGRADE_ITEM_COMMENT, java.lang.String.format(DEFAULT_STRING, "nether_star"))
            .defineInList("tier4.upgrade_item", "minecraft:nether_star", ALL_ITEMS);
    public static final ModConfigSpec.IntValue TIER_4_GEN_INCREASE = BUILDER
            .comment(GEN_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 10000))
            .defineInRange("tier4.gen_increase", 10000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_4_TRANSFER_INCREASE = BUILDER
            .comment(TRANSFER_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 20000))
            .defineInRange("tier4.transfer_increase", 20000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_4_CAPACITY_INCREASE = BUILDER
            .comment(CAPACITY_INCREASE_COMMENT, java.lang.String.format(DEFAULT_STRING, 100000000))
            .defineInRange("tier4.capacity_increase", 100000000, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue TIER_4_COLOR = BUILDER
            .comment(COLOR_COMMENT, java.lang.String.format(DEFAULT_STRING, 14142935))
            .defineInRange("tier4.color", 14142935, 0, MAX_RGB);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean shouldAlwayGenerate;
    public static String defaultColorName;

    public static int initialGenerationRate;
    public static int initialTransferRate;
    public static int initialMaxEnergy;

    public static int maxGenerationRate;
    public static int maxTransferRate;
    public static int maxCapacity;

    public static Map<Item, Tier> tierMap = new HashMap<>();


//    private static boolean validateItemName(final Object obj) {
//        return obj instanceof final String itemName && BuiltInRegistries.ITEM.containsKey(new ResourceLocation(itemName));
//    }
//
//    private static boolean validateColorName(final Object obj) {
//        return obj instanceof final String colorName && GroupColor.isColor(colorName);
//    }

//    private static boolean validateColor(final Object obj) {
//        return obj instanceof final List<?> list && list.size() == 3
//                && list.stream().allMatch(c -> c instanceof Integer && (int) c >= 0 && (int) c <= 255);
//    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        initialGenerationRate = INITIAL_GENERATION_RATE.get();
        initialTransferRate = INITIAL_TRANSFER_RATE.get();
        initialMaxEnergy = INITIAL_CAPACITY.get();

        shouldAlwayGenerate = ALWAYS_GENERATE.get();
        defaultColorName = DEFAULT_COLOR.get().getSerializedName();

        maxGenerationRate = MAX_GENERATION_RATE.get();
        maxTransferRate = MAX_TRANSFER_RATE.get();
        maxCapacity = MAX_CAPACITY.get();

        int t1Color = TIER_1_COLOR.get();
        tierMap.put(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_1_UPGRADE_ITEM.get())),
                new Tier(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_1_UPGRADE_ITEM.get())),
                        TIER_1_GEN_INCREASE.get(), TIER_1_TRANSFER_INCREASE.get(), TIER_1_CAPACITY_INCREASE.get(),
                        new Color(t1Color)
                )
        );
        int t2Color = TIER_2_COLOR.get();
        tierMap.put(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_2_UPGRADE_ITEM.get())),
                new Tier(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_2_UPGRADE_ITEM.get())),
                        TIER_2_GEN_INCREASE.get(), TIER_2_TRANSFER_INCREASE.get(), TIER_2_CAPACITY_INCREASE.get(),
                        new Color(t2Color)
                )
        );
        int t3Color = TIER_3_COLOR.get();
        tierMap.put(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_3_UPGRADE_ITEM.get())),
                new Tier(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_3_UPGRADE_ITEM.get())),
                        TIER_3_GEN_INCREASE.get(), TIER_3_TRANSFER_INCREASE.get(), TIER_3_CAPACITY_INCREASE.get(),
                        new Color(t3Color)
                )
        );
        int t4Color = TIER_4_COLOR.get();
        tierMap.put(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_4_UPGRADE_ITEM.get())),
                new Tier(BuiltInRegistries.ITEM.get(new ResourceLocation(TIER_4_UPGRADE_ITEM.get())),
                        TIER_4_GEN_INCREASE.get(), TIER_4_TRANSFER_INCREASE.get(), TIER_4_CAPACITY_INCREASE.get(),
                        new Color(t4Color)
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
