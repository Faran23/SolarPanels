package io.github.faran23.solarpanels;

import net.minecraft.util.StringRepresentable;

import java.awt.*;

public enum GroupColor implements StringRepresentable {

    WHITE("white", "#ffffff"),
    ORANGE("orange", "#d8840a"),
    MAGENTA("magenta", "#ff32ff"),
    LIGHT_BLUE("light_blue", "#57b7fe"),
    YELLOW("yellow", "#f4db48"),
    LIME("lime", "#49ce4e"),
    PINK("pink", "#f58be3"),
    GRAY("gray", "#434343"),
    LIGHT_GRAY("light_gray", "#8c8c8c"),
    CYAN("cyan", "#27b5b5"),
    PURPLE("purple", "#7f2fba"),
    BLUE("blue", "#044b8f"),
    BROWN("brown", "#582d02"),
    GREEN("green", "#044d14"),
    RED("red", "#c72525"),
    BLACK("black", "#202020");

    private final String name;
    private final String hexString;

    private GroupColor(String name, String hexString) {
        this.name = name;
        this.hexString = hexString;
    }

    public static GroupColor fromString(String name) {
        GroupColor color;
        try {
            color = GroupColor.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            color = WHITE;
        }
        return color;

    }

    public static boolean isColor(String string) {
        try {
            GroupColor.valueOf(string.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Color getColor() {
        return Color.decode(hexString);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int getRGB() {
        return getColor().getRGB();
    }
}
