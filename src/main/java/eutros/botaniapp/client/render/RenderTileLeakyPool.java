package eutros.botaniapp.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import eutros.botaniapp.common.block.tile.TileLeakyPool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.helper.ShaderHelper;

public class RenderTileLeakyPool extends TileEntityRenderer<TileLeakyPool> {

    public static final VertexFormat POSITION_TEX_LMAP =
            new VertexFormat()
                    .addElement(DefaultVertexFormats.POSITION_3F)
                    .addElement(DefaultVertexFormats.TEX_2F)
                    .addElement(DefaultVertexFormats.TEX_2S);

    @Override
    public void render(TileLeakyPool pool, double d0, double d1, double d2, float f, int digProgress) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableRescaleNormal();

        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GlStateManager.translated(d0, d1, d2);

        Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.translatef(0.5F, 1.5F, 0.5F);
        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.enableRescaleNormal();

        int mana = pool.getCurrentMana();
        int cap = TileLeakyPool.MAX_MANA;

        float waterLevel = (float) mana / (float) cap * 0.4F;

        if(waterLevel > 0) {
            float s = 1F / 256F * 14F;
            float v = 1F / 8F;
            float w = -v * 3.5F;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableAlphaTest();
            GlStateManager.color4f(1F, 1F, 1F, 1F);

            ShaderHelper.useShader(ShaderHelper.manaPool);

            // TODO ;-; put the mana in the pool
            GlStateManager.translatef(w, -1.43F, w);
            GlStateManager.translatef(0F, waterLevel, 0F);
            GlStateManager.scalef(s, s, s);
            GlStateManager.rotatef(90F, 1F, 0F, 0F);
            renderIcon(MiscellaneousIcons.INSTANCE.manaWater);
            GlStateManager.rotatef(180F, 0F, 1F, 0F);
            GlStateManager.translatef(-8F, s*waterLevel, 0F);
            renderIcon(MiscellaneousIcons.INSTANCE.manaWater);


            ShaderHelper.releaseShader();

            GlStateManager.enableAlphaTest();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        ItemStack stack = pool.getItemHandler().getStackInSlot(0);

        if(!stack.isEmpty()) {
            Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            stack.getItem();
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0F, -1.53F, 0F);
            GlStateManager.rotatef(90, 1F, 0, 0);
            GlStateManager.scalef(1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }

    private void renderIcon(TextureAtlasSprite par3Icon) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, POSITION_TEX_LMAP);
        tessellator.getBuffer().pos(0, 16, 0).tex(par3Icon.getMinU(), par3Icon.getMaxV()).lightmap(240, 240).endVertex();
        tessellator.getBuffer().pos(16, 16, 0).tex(par3Icon.getMaxU(), par3Icon.getMaxV()).lightmap(240, 240).endVertex();
        tessellator.getBuffer().pos(16, 0, 0).tex(par3Icon.getMaxU(), par3Icon.getMinV()).lightmap(240, 240).endVertex();
        tessellator.getBuffer().pos(0, 0, 0).tex(par3Icon.getMinU(), par3Icon.getMinV()).lightmap(240, 240).endVertex();
        tessellator.draw();
    }
}
