package eutros.botaniapp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import eutros.botaniapp.api.internal.config.Configurable;
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
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.client.core.handler.MiscellaneousIcons;

import java.util.function.Function;

public class RenderTileLeakyPool extends TileEntityRenderer<TileLeakyPool> {

    @Configurable(side = ModConfig.Type.CLIENT,
                  path = {"render", "leaky_pool"},
                  comment = "How long should the Leaky Mana Pool's drip be, at most. (In 1/16s of a block)")
    public static float DRIP_LENGTH = 0.1F;

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

            IVertexBuilder buffer = buffers.getBuffer(RenderHelper.ICON_OVERLAY);
            TextureAtlasSprite icon = MiscellaneousIcons.INSTANCE.manaWater;

            ms.translate(w, -1.43F + waterLevel, w);
            ms.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90F));

            ms.scale(s, s, s);
            renderIcon(ms, buffer, 0, 0, icon, 16, 16, 1);

            ms.pop();

            float dripFrequency = pool.getDripFrequency();

            final float root = 0.154F;
            // Function mapping a linear drip level to something more realistic I guess.
            final Function<Float, Float> dripFunc = l -> {
                float progress = 1 + l + (ClientTickHandler.partialTicks / dripFrequency);
                return Math.min(
                        (float) Math.sin(
                                Math.PI *
                                        (Math.pow(
                                                (progress - root)
                                                        % 1F,
                                                5)
                                        )
                        ),
                        pool.getLastShot() == -1 && progress - 1 < root ?
                        0 : 1
                );
            };

            float dripLevel = dripFrequency < 3 || !pool.canLeak(TileLeakyPool.CLEAR_SQUARES) ?
                              0 :
                              dripFunc.apply(pool.getDripProgress()) * DRIP_LENGTH;

            ms.push();

            s = 1F / 16F;

            ms.scale(s, s, s);
            ms.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(90F));

            ms.translate(-2F, -2F, -24F - dripLevel);
            renderIcon(ms, buffer, 0, 0, icon, 4, 4, 1);

            if(dripLevel > 0) {
                ms.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(90F));
                ms.scale(1F, dripLevel / 4, 1F);
                ms.translate(0F, -4F, 0F);
                renderIcon(ms, buffer, 0, 0, icon, 4, 4, 1);

                for(int i = 0; i < 3; i++) {
                    ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90F));
                    ms.translate(-4F, 0F, 0);
                    renderIcon(ms, buffer, 0, 0, icon, 4, 4, 1);
                }
            }

            ms.pop();
        }

        ItemStack stack = pool.getItemHandler().getStackInSlot(0);

        if(!stack.isEmpty()) {

            ms.push();

            ms.translate(0F, -1.53F, 0F);
            ms.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90F));
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.NONE, light, overlay, ms, buffers);
            ms.pop();
        }

        ms.pop();
    }

}
