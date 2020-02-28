package eutros.botaniapp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import eutros.botaniapp.common.block.tile.TileChargingPlate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderTileChargingPlate extends TileEntityRenderer<TileChargingPlate> {

	public RenderTileChargingPlate(TileEntityRendererDispatcher p_i226006_1_) {
		super(p_i226006_1_);
	}

	@Override
	public void render(TileChargingPlate plate, float v, MatrixStack ms, @NotNull IRenderTypeBuffer buffers, int light, int overlay) {
		ms.push();
		ms.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
		ms.translate(1.0F, -0.125F, -0.25F);
		ItemStack stack = plate.getItemHandler().getStackInSlot(0);
		if(!stack.isEmpty()) {
			ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
			ms.translate(0.5F, 0.5F, 0);
			Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, light, overlay, ms, buffers);
		}
		ms.pop();
	}
}
