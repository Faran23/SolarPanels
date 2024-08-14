package io.github.faran23.solarpanels.compat.jei;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.faran23.solarpanels.Config;
import io.github.faran23.solarpanels.register.Registration;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import java.awt.*;
import java.util.List;

import static io.github.faran23.solarpanels.Utils.humanReadableNumberNoUnit;

public class UpgradeWrapper implements IRecipeCategoryExtension, IRecipeSlotTooltipCallback {
    private final Config.Tier upgrade;
    public static final ModelData MODEL_DATA = ModelData.builder().with(new ModelProperty<>(), true).build();

    public UpgradeWrapper(Config.Tier upgrade) {
        this.upgrade = upgrade;
    }

    public Item getUpgradeItem() {
        return upgrade.upgradeItem;
    }

    // Mostly taken from create mod's code <3
    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        BlockState state = Registration.SOLAR_BLOCK.get().defaultBlockState();

        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        stack.translate(UpgradeCategory.WIDTH / 2f - 24, UpgradeCategory.HEIGHT - 24, 100);

        stack.mulPose(Axis.XP.rotationDegrees(215f));
        stack.mulPose(Axis.YP.rotationDegrees(15f));

//        //45, 5, 15 - old values
//        stack.mulPose(Axis.XP.rotationDegrees(180f));
//        stack.mulPose(Axis.XP.rotationDegrees(45f));
//        stack.mulPose(Axis.YP.rotationDegrees(5f));
//        stack.mulPose(Axis.ZP.rotationDegrees(15f));
        stack.scale(40, 40, 40);

        Lighting.setupFor3DItems();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Color c = new Color(0, 0, 255); // colours not working here? just use a blue for now

        BakedModel model = blockRenderer.getBlockModel(state);
        for (RenderType chunkType : model.getRenderTypes(state, RandomSource.create(42L), MODEL_DATA)) {
            RenderType rt = RenderTypeHelper.getEntityRenderType(chunkType, true);
            blockRenderer.getModelRenderer()
                    .renderModel(stack.last(), buffer.getBuffer(rt), state, model, c.getRed(), c.getGreen(), c.getBlue(),
                            LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, MODEL_DATA, chunkType);
        }

        buffer.endBatch();
        stack.popPose();
    }

    @Override
    public void onTooltip(IRecipeSlotView iRecipeSlotView, List<Component> list) {
        iRecipeSlotView.getSlotName().ifPresent(s -> list.add(Component.translatable(s)));
        iRecipeSlotView.getDisplayedItemStack().ifPresent(itemStack -> {
            if (Config.tierMap.containsKey(itemStack.getItem())) {
                Item item = itemStack.getItem();
                Config.Tier tier = Config.getTier(item);
                list.add(Component.translatable("jei.tooltip.solar_panels.gen_increase").withStyle(ChatFormatting.GREEN)
                        .append(Component.literal(": " + humanReadableNumberNoUnit(tier.genIncrease, false) + " FE/t")));
                list.add(Component.translatable("jei.tooltip.solar_panels.transfer_increase").withStyle(ChatFormatting.GREEN)
                        .append(Component.literal(": " + humanReadableNumberNoUnit(tier.transferIncrease, false) + " FE/t")));
                list.add(Component.translatable("jei.tooltip.solar_panels.capacity_increase").withStyle(ChatFormatting.GREEN)
                        .append(Component.literal(": " + humanReadableNumberNoUnit(tier.capacityIncrease, false) + " FE")));
            }
        });
    }
}
