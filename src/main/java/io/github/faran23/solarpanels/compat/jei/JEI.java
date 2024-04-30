package io.github.faran23.solarpanels.compat.jei;

import io.github.faran23.solarpanels.Config;
import io.github.faran23.solarpanels.SolarPanels;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@MethodsReturnNonnullByDefault
public class JEI implements IModPlugin {

    public static final ResourceLocation JEI_ID = new ResourceLocation(SolarPanels.MODID, "jei");
    public static final RecipeType<UpgradeWrapper> UPGRADE_RECIPE_TYPE =
            new RecipeType<>(new ResourceLocation(SolarPanels.MODID, "upgrade_recipe"), UpgradeWrapper.class);

    @Override
    public ResourceLocation getPluginUid() {
        return JEI_ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(UPGRADE_RECIPE_TYPE, getUpgradeRecipes());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new UpgradeCategory(registration.getJeiHelpers().getGuiHelper()));

    }

    public List<UpgradeWrapper> getUpgradeRecipes() {
        ArrayList<UpgradeWrapper> recipes = new ArrayList<>();
        Config.tierMap.forEach((tier, config) -> recipes.add(new UpgradeWrapper(config)));
        return recipes;
    }
}
