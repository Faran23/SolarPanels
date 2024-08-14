package io.github.faran23.solarpanels.compat.jei;

import io.github.faran23.solarpanels.config.Config;
import io.github.faran23.solarpanels.SolarPanels;
import io.github.faran23.solarpanels.register.Registration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@MethodsReturnNonnullByDefault
public class JEI implements IModPlugin {

    public static final ResourceLocation JEI_ID = ResourceLocation.fromNamespaceAndPath(SolarPanels.MODID, "jei");
    public static final RecipeType<UpgradeWrapper> UPGRADE_RECIPE_TYPE =
            new RecipeType<>(ResourceLocation.fromNamespaceAndPath(SolarPanels.MODID, "upgrade_recipe"), UpgradeWrapper.class);

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

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Registration.SOLAR_BLOCK.get()), UPGRADE_RECIPE_TYPE);
    }

    public List<UpgradeWrapper> getUpgradeRecipes() {
        ArrayList<UpgradeWrapper> recipes = new ArrayList<>();
        Config.tierMap.forEach((tier, config) -> recipes.add(new UpgradeWrapper(config)));
        return recipes;
    }
}
