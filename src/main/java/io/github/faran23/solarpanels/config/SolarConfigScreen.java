package io.github.faran23.solarpanels.config;


import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import io.github.faran23.solarpanels.GroupColor;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Some simple and ugly overrides to add some readability to coloured values in the config screen
 */
@ParametersAreNonnullByDefault
public class SolarConfigScreen extends ConfigurationScreen.ConfigurationSectionScreen {

    private static final String LANG_PREFIX = "neoforge.configuration.uitext.";
    private static final String SECTION = LANG_PREFIX + "section";
    private static final String SECTION_TEXT = LANG_PREFIX + "sectiontext";
    protected static final ConfigurationScreen.TranslationChecker translationChecker = new ConfigurationScreen.TranslationChecker();

    public SolarConfigScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title) {
        super(parent, type, modConfig, title);
    }

    public SolarConfigScreen(final Context parentContext, final Screen parent, final Map<String, Object> valueSpect, final String key, final Set<? extends UnmodifiableConfig.Entry> entrySet, Component title) {
        super(parentContext, parent, valueSpect, key, entrySet, title);
    }

    @Nullable
    @Override
    protected Element createSection(String key, UnmodifiableConfig subconfig, UnmodifiableConfig subsection) {
        if (subconfig.isEmpty()) return null;
        return new Element(Component.translatable(SECTION, getTranslationComponent(key)), getTooltipComponent(key, null),
                Button.builder(Component.translatable(SECTION, Component.translatable(translationChecker.check(getTranslationKey(key) + ".button", SECTION_TEXT))),
                                button -> minecraft.setScreen(sectionCache.computeIfAbsent(key,
                                        k -> new SolarConfigScreen(context, this, subconfig.valueMap(), key, subsection.entrySet(), Component.translatable(getTranslationKey(key))).rebuild())))
                        .tooltip(Tooltip.create(getTooltipComponent(key, null)))
                        .width(Button.DEFAULT_WIDTH)
                        .build(),
                false);
    }

    @Nullable
    @Override
    protected <T extends Number & Comparable<? super T>> Element createNumberBox(String key, ModConfigSpec.ValueSpec spec, Supplier<T> source, Consumer<T> target, @Nullable Predicate<T> tester, Function<String, T> parser, T zero) {

        if (key.toLowerCase().contains("color")) {

            final ModConfigSpec.Range<T> range = spec.getRange();
            final EditBox box = new EditBox(font, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, getTranslationComponent(key));
            box.setEditable(true);
            box.setFilter(newValueString -> {
                try {
                    parser.apply(newValueString);
                    return true;
                } catch (final NumberFormatException e) {
                    return isPartialNumber(newValueString, (range == null || range.getMin().compareTo(zero) < 0));
                }
            });
            box.setTooltip(Tooltip.create(getTooltipComponent(key, range)));
            box.setValue(source.get() + "");
            box.setTextColor(source.get().intValue()); // set initial colour
            box.setMaxLength(8);
            box.setResponder((newString) -> {
                int color = newString.isEmpty() ? 0xFF0000 : 0xFFFFFF; // red if empty, default to white otherwise
                try {
                    final T newValue = parser.apply(newString);
                    if (tester != null ? tester.test(newValue) : (newValue != null && (range == null || range.test(newValue)) && spec.test(newValue))) {
                        color = Integer.parseInt(newString);
                        if (!newValue.equals(source.get())) {
                            if (color > 0xFFFFFF) { // set values over max to max, re-call to set colour correctly
                                box.setValue(String.valueOf(0xFFFFFF));
                                return;
                            }
                            undoManager.add(v -> {
                                target.accept(v);
                                onChanged(key);
                            }, newValue, v -> {
                                target.accept(v);
                                onChanged(key);
                            }, source.get());
                        }
                    }
                } catch (NumberFormatException | NullPointerException ignored) {} // will catch if empty, red colour applied below
                box.setTextColor(getReadableColor(color));
            });
            return new Element(getTranslationComponent(key), getTooltipComponent(key, null), box);
        }
        return super.createNumberBox(key, spec, source, target, tester, parser, zero);
    }

    @Nullable
    @Override
    protected <T extends Enum<T>> Element createEnumValue(String key, ModConfigSpec.ValueSpec spec, Supplier<T> source, Consumer<T> target) {
        if (key.equalsIgnoreCase("default_color")) {
            @SuppressWarnings("unchecked")
            final Class<T> clazz = (Class<T>) spec.getClazz();
            assert clazz != null;

            final List<T> list = Arrays.stream(clazz.getEnumConstants()).filter(spec::test).toList();

            return new Element(getTranslationComponent(key), getTooltipComponent(key, null),
                    new OptionInstance<>(getTranslationKey(key), getTooltip(key, null), (caption, displayvalue) -> displayvalue instanceof TranslatableEnum tenum ? tenum.getTranslatedName() : Component.literal(displayvalue.name()).withColor(displayvalue instanceof GroupColor ? ((GroupColor) displayvalue).getRGB() : 0xFFFFFF),
                            new Custom<>(list), source.get(), newValue -> {
                        // regarding change detection: new value always is different (cycle button)
                        undoManager.add(v -> {
                            target.accept(v);
                            onChanged(key);
                        }, newValue, v -> {
                            target.accept(v);
                            onChanged(key);
                        }, source.get());
                    }));
        }
        return super.createEnumValue(key, spec, source, target);
    }

    /**
     * Used to prevent dark colours from being unreadable
     */
    private int getReadableColor(int color) {
        if (color < 0 || color > 0xFFFFFF) return 0xFF0000;
        Color newColor = new Color(color);
        int red = newColor.getRed();
        int green = newColor.getGreen();
        int blue = newColor.getBlue();
        if (newColor.getRed() < 50) {
            red = 50;
        }
        if (newColor.getGreen() < 50) {
            green = 50;
        }
        if (newColor.getBlue() < 50) {
            blue = 50;
        }

        return new Color(red, green, blue).getRGB();
    }

}
