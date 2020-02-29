/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jul 8, 2014, 10:58:46 PM (GMT)]
 */
package eutros.botaniapp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import eutros.botaniapp.client.core.handler.ClientTickHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.state.BotaniaStateProps;

import javax.annotation.Nonnull;
import java.util.Random;

public class RenderTileFloatingFlower extends TileEntityRenderer<TileEntity> {

    public RenderTileFloatingFlower(TileEntityRendererDispatcher manager) {
        super(manager);
    }

    @Override
    public void render(@Nonnull TileEntity tile, float t, @NotNull MatrixStack ms, @NotNull IRenderTypeBuffer buffers, int light, int overlay) {

        IModelData data = tile.getModelData();
        if (!data.hasProperty(BotaniaStateProps.FLOATING_DATA))
            return;

        ms.push();

        double worldTime = ClientTickHandler.ticksInGame + t;
        if(tile.getWorld() != null)
            worldTime += new Random(tile.getPos().hashCode()).nextInt(1000);

        ms.translate(0.5F, 0, 0.5F);
        ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-((float) worldTime * 0.5F)));
        ms.translate(-0.5, (float) Math.sin(worldTime * 0.05F) * 0.1F, 0.5);

        ms.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(4F * (float) Math.sin(worldTime * 0.04F)));
        ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));

        BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRendererDispatcher();
        BlockState state = tile.getBlockState();

        IBakedModel ibakedmodel = brd.getModelForState(state);
        brd.getBlockModelRenderer().renderModel(ms.peek(), buffers.getBuffer(RenderTypeLookup.getEntityBlockLayer(state)), state, ibakedmodel, 1, 1, 1, light, overlay, data);

        ms.pop();

    }

}
