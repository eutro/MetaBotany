package eutros.botaniapp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import eutros.botaniapp.client.core.handler.ClientTickHandler;
import eutros.botaniapp.common.block.tinkerer.tile.TileFrameTinkerer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RenderTileFrameTinkerer extends TileEntityRenderer<TileFrameTinkerer> {

	public RenderTileFrameTinkerer(TileEntityRendererDispatcher p_i226006_1_) {
		super(p_i226006_1_);
	}

	@Override
	public void render(TileFrameTinkerer plate, float v, MatrixStack ms, @NotNull IRenderTypeBuffer buffers, int light, int overlay) {
		ms.push();
		ItemStack stack = plate.getItemHandler().getStackInSlot(0);
		if(!stack.isEmpty()) {
			Random rand = new Random(plate.hashCode());
			float angle = (ClientTickHandler.total + 360F * rand.nextFloat()) * 2;
			int period = 10;
			float height = (float) Math.sin(ClientTickHandler.total / period + Math.PI * rand.nextFloat());
			ms.translate(0.5F, 0.5F + height * 0.125, 0.5F);
			ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(angle));
			Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, light, overlay, ms, buffers);
		}
		ms.pop();
	}
}
