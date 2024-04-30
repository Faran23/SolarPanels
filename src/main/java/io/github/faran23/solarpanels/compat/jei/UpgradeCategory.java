package io.github.faran23.solarpanels.compat.jei;

import io.github.faran23.solarpanels.SolarPanels;
import io.github.faran23.solarpanels.register.Registration;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UpgradeCategory implements IRecipeCategory<UpgradeWrapper> {

    private static final ResourceLocation ARROW_TEX = new ResourceLocation(SolarPanels.MODID, "textures/gui/jei_arrow.png");
    public static final int WIDTH = 50;
    public static final int HEIGHT = 70;

    private final IGuiHelper guiHelper;

    public UpgradeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public @NotNull RecipeType<UpgradeWrapper> getRecipeType() {
        return JEI.UPGRADE_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.solar_panels.upgrade");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return guiHelper.createBlankDrawable(WIDTH, HEIGHT);
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return guiHelper.createDrawableItemStack(new ItemStack(Registration.SOLAR_BLOCK.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, UpgradeWrapper upgradeWrapper, @NotNull IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, WIDTH / 2 - 8, 3).addItemStack(upgradeWrapper.getUpgradeItem().getDefaultInstance())
                .addTooltipCallback(upgradeWrapper).setSlotName("jei.tooltip.solar_panels.upgrade_item");
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Registration.SOLAR_BLOCK.get()));
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Registration.SOLAR_BLOCK.get()));
    }

    @Override
    public void draw(UpgradeWrapper recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(ARROW_TEX, WIDTH / 2 - 8, HEIGHT / 2 - 13, 0, 0, 0, 16, 16, 16, 16);
        recipe.drawInfo(recipe, getWidth(), getHeight(), guiGraphics, mouseX, mouseY);
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(UpgradeWrapper recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        return recipe.getTooltipStrings(recipe, mouseX, mouseY);
    }
}
