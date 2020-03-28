package eutros.botaniapp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import eutros.botaniapp.client.core.handler.ClientTickHandler;
import eutros.botaniapp.common.block.tile.TileLeakyPool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.client.core.handler.MiscellaneousIcons;

public class RenderTileLeakyPool extends TileEntityRenderer<TileLeakyPool> {

    public RenderTileLeakyPool(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    public static void renderIcon(MatrixStack ms, IVertexBuilder buffer, int x, int y, TextureAtlasSprite icon, int width, int height, float alpha) {
        Matrix4f mat = ms.peek().getModel();
        int fullBrightness = 0xF000F0;
        buffer.vertex(mat, x, y + height, 0).color(1, 1, 1, alpha).texture(icon.getMinU(), icon.getMaxV()).light(fullBrightness).endVertex();
        buffer.vertex(mat, x + width, y + height, 0).color(1, 1, 1, alpha).texture(icon.getMaxU(), icon.getMaxV()).light(fullBrightness).endVertex();
        buffer.vertex(mat, x + width, y, 0).color(1, 1, 1, alpha).texture(icon.getMaxU(), icon.getMinV()).light(fullBrightness).endVertex();
        buffer.vertex(mat, x, y, 0).color(1, 1, 1, alpha).texture(icon.getMinU(), icon.getMinV()).light(fullBrightness).endVertex();
    }

    @Override
    public void render(@NotNull TileLeakyPool pool, float f, @NotNull MatrixStack ms, @NotNull IRenderTypeBuffer buffers, int light, int overlay) {

        ms.push();
        ms.translate(0.5F, 1.5F, 0.5F);

        int mana = pool.getCurrentMana();
        int cap = TileLeakyPool.MAX_MANA;

        float waterLevel = (float) mana / (float) cap * 0.4F;

        if(waterLevel > 0) {
            float s = 1F / 256F * 14F;
            float v = 1F / 8F;
            float w = -v * 3.5F;

            ms.push();

            float dripFrequency = pool.getDripFrequency();
            final float MAX_DRIP = 1.2F;
            float dripLevel = dripFrequency < 10 ?
                              MAX_DRIP :
                              pool.getDripPercentage(ClientTickHandler.partialTicks) * MAX_DRIP;

            ms.translate(w, -1.43F + waterLevel, w);
            ms.scale(s, s, s);
            ms.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90F));
            IVertexBuilder buffer = buffers.getBuffer(RenderHelper.ICON_OVERLAY);
            renderIcon(ms, buffer, 0, 0, MiscellaneousIcons.INSTANCE.manaWater, 16, 16, 1);
            ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180F));
            ms.translate(-16F, 0F, -waterLevel / s - dripLevel);
            renderIcon(ms, buffer, 0, 0, MiscellaneousIcons.INSTANCE.manaWater, 16, 16, 1);

            ms.pop();
        }

        ItemStack stack = pool.getItemHandler().getStackInSlot(0);

        if(!stack.isEmpty()) {

            ms.push();

            ms.translate(0F, -1.53F, 0F);
            ms.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90F));
            ms.scale(1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.NONE, light, overlay, ms, buffers);
            ms.pop();
        }

        ms.pop();
    }

}
