package io.github.faran23.solarpanels.datagen;


import io.github.faran23.solarpanels.register.Registration;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class SolarRecipeProvider extends RecipeProvider {

    public static final String RECIPE_GROUP = "solar_panels.recipe_group";

    public SolarRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.SOLAR_PANEL_ITEM.get())
                .pattern("GGG")
                .pattern("IRI")
                .pattern("III")
                .define('G', Tags.Items.GLASS)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .group(RECIPE_GROUP)
                .save(consumer);
    }
}
