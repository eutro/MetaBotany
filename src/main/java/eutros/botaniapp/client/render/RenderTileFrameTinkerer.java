package eutros.botaniapp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import eutros.botaniapp.api.internal.config.Configurable;
import eutros.botaniapp.client.core.handler.ClientTickHandler;
import eutros.botaniapp.common.block.tinkerer.tile.TileFrameTinkerer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class RenderTileFrameTinkerer extends TileEntityRenderer<TileFrameTinkerer> {

    public RenderTileFrameTinkerer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Configurable(path = {"render", "frame_tinkerer"},
                  side = ModConfig.Type.CLIENT,
                  comment = "The time, in ticks, that it takes for an item to reach its maximum height from its minimum height.")
    public static float OSC_PERIOD = 10;

    @Configurable(path = {"render", "frame_tinkerer"},
                  side = ModConfig.Type.CLIENT,
                  comment = "The time, in ticks, that it takes for an item to rotate around once.")
    public static float ROTATION_PERIOD = 100;

    @Configurable(path = {"render", "frame_tinkerer"},
                  side = ModConfig.Type.CLIENT,
                  comment = "The distance, in blocks, between the maximum and minimum height of the item.")
    public static float HEIGHT_DIFF = 0.25F;

    @Configurable(path = {"render", "frame_tinkerer"},
                  side = ModConfig.Type.CLIENT,
                  comment = "Disable Item Frame Tinkerer special rendering.")
    public static boolean DISABLE = false;

    @ParametersAreNonnullByDefault
    @Override
    public void render(TileFrameTinkerer plate, float v, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        if(DISABLE ||
        OSC_PERIOD * ROTATION_PERIOD == 0)
            return;

        ItemStack stack = plate.getItemHandler().getStackInSlot(0);

        if(stack.isEmpty())
            return;

        ms.push();
        Random rand = new Random(plate.hashCode());
        float height = (float) Math.sin(ClientTickHandler.total / OSC_PERIOD + Math.PI * rand.nextFloat());
        ms.translate(0.5F, HEIGHT_DIFF + 0.2 + height * HEIGHT_DIFF / 2, 0.5F);
        ms.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float) (ClientTickHandler.total * Math.PI / ROTATION_PERIOD + Math.PI * rand.nextFloat())));
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, light, overlay, ms, buffers);
        ms.pop();
    }

}
