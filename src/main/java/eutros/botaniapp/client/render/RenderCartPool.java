package eutros.botaniapp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import eutros.botaniapp.client.core.handler.ClientTickHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.data.EmptyModelData;
import vazkii.botania.client.core.handler.MiscellaneousIcons;

import java.util.Random;

import static eutros.botaniapp.client.render.RenderTileLeakyPool.renderIcon;


public class RenderCartPool {

    public static void render(BlockState state, boolean fab, int cap, int mana, TileEntity pool, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {

        ms.push();

        if(fab) {
            float time = ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks;
            time += new Random(pool.getPos().getX() ^ pool.getPos().getY() ^ pool.getPos().getZ()).nextInt(100000);
            int color = MathHelper.hsvToRGB(time * 0.005F, 0.6F, 1F);

            int red = (color & 0xFF0000) >> 16;
            int green = (color & 0xFF00) >> 8;
            int blue = color & 0xFF;
            IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(state);
            IVertexBuilder buffer = buffers.getBuffer(RenderTypeLookup.getEntityBlockLayer(state));
            Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer()
                    .renderModel(ms.peek(), buffer, state, model, red / 255F, green / 255F, blue / 255F, light, overlay, EmptyModelData.INSTANCE);
        }

        ms.translate(0.5F, 1.5F, 0.5F);

        float waterLevel = (float) mana / (float) cap * 0.4F;

        float s;
        float v = 1F / 8F;
        float w = -v * 3.5F;

        if(waterLevel > 0) {
            s = 1F / 256F * 14F;
            ms.push();
            ms.translate(w, -1F - (0.43F - waterLevel), w);
            ms.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90F));
            ms.scale(s, s, s);

            IVertexBuilder buffer = buffers.getBuffer(RenderHelper.ICON_OVERLAY);
            renderIcon(ms, buffer, 0, 0, MiscellaneousIcons.INSTANCE.manaWater, 16, 16, 1);

            ms.pop();
        }
        ms.pop();

    }
}