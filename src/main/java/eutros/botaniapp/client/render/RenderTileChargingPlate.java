package eutros.botaniapp.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import eutros.botaniapp.common.block.tile.TileChargingPlate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class RenderTileChargingPlate extends TileEntityRenderer<TileChargingPlate> {

	@Override
	public void render(@Nonnull TileChargingPlate te, double d0, double d1, double d2, float pt, int digProgress) {
		GlStateManager.pushMatrix();
		GlStateManager.translated(d0, d1, d2);
		GlStateManager.rotatef(90F, 1F, 0F, 0F);
		GlStateManager.translatef(1.0F, -0.125F, -0.25F);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		ItemStack stack = te.getItemHandler().getStackInSlot(0);
		if(!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
			float s = 1.0F;
			GlStateManager.scalef(s, s, s);
			GlStateManager.rotatef(180F, 0F, 1F, 0F);
			GlStateManager.translatef(0.5F, 0.5F, 0);
			Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.popMatrix();
		}
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
	}

}
